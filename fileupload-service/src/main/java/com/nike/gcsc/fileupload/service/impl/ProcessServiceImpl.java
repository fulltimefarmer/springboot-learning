package com.nike.gcsc.fileupload.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DefaultTuple;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nike.gcsc.common.GlobalResponse;
import com.nike.gcsc.fileupload.config.AmazonS3Configuration.S3Config;
import com.nike.gcsc.fileupload.config.FileUploadConfig;
import com.nike.gcsc.fileupload.constant.ErrorConstants;
import com.nike.gcsc.fileupload.dto.request.BaseDto;
import com.nike.gcsc.fileupload.dto.request.PostUploadRequestDto;
import com.nike.gcsc.fileupload.dto.request.PreUploadRequestDto;
import com.nike.gcsc.fileupload.dto.response.MultiUploadResponseDto;
import com.nike.gcsc.fileupload.dto.response.SliceInfo;
import com.nike.gcsc.fileupload.dto.response.UploadResponseDto;
import com.nike.gcsc.fileupload.service.ProcessService;
import com.nike.gcsc.fileupload.util.FileUtils;
import com.nike.gcsc.fileupload.util.ForEachUtils;
import com.nike.gcsc.fileupload.util.RedisKeyUtils;
import com.nike.gcsc.fileupload.util.RedisUtils;
import com.nike.gcsc.fileupload.util.S3ClientUtils;
import com.nike.gcsc.fileupload.util.SliceUtils;
import com.nike.gcsc.util.DateUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

/**
 * @author roger yang
 * @date 10/31/2019
 * S3 API:
    1、S3Client.createMultipartUpload
    2、S3Client.uploadPart
    3、S3Client.completeMultipartUpload
    4、S3Client.putObject
 */
@Service
@Slf4j
public class ProcessServiceImpl implements ProcessService {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private FileUploadConfig bigfileConfig;
    
    private static ThreadLocal<S3Client> clientLocal = new ThreadLocal<>();
    private static ThreadLocal<S3Config> configLocal = new ThreadLocal<>();

    @Override
    public Mono<GlobalResponse<UploadResponseDto>> preUpload(PreUploadRequestDto dto) {
        RLock lock = redissonClient.getLock(RedisKeyUtils.getFileLock(dto));
        boolean lockFlag = false;
        try {
            lockFlag = lock.tryLock(5, bigfileConfig.getFileLockTimeOutSecond(), TimeUnit.SECONDS);
            if(!lockFlag) {
                log.warn("Another person is uploading， MD5 is :{}", dto.getMd5());
                return Mono.just(GlobalResponse.buildFail(ErrorConstants.FILE_IS_UPLOADING_ERROR));
            }
            setThreadLocal(dto);
            
            String hashRedisKey = RedisKeyUtils.getBigFileInfoHashKey(dto);
            // if hasKey, this file is not first upload
            if (RedisUtils.hasKey(hashRedisKey)) {
                return secondPreUpload(dto, hashRedisKey);
            }
            
            return initPreUpload(dto, hashRedisKey);
        } catch (InterruptedException e) {
            log.error("redis lock is interrupted", e);
            return Mono.just(GlobalResponse.buildFail(ErrorConstants.FILE_IS_UPLOADING_ERROR));
        } finally {
            if(lockFlag) {
                lock.unlock();
            }
            removeThreadLocal();
        }
    }
    
    @Override
    public Mono<GlobalResponse<MultiUploadResponseDto>> preMultiUpload(PreUploadRequestDto dto) {
        RLock lock = redissonClient.getLock(RedisKeyUtils.getFileLock(dto));
        boolean lockFlag = false;
        try {
            lockFlag = lock.tryLock(5, bigfileConfig.getFileLockTimeOutSecond(), TimeUnit.SECONDS);
            if(!lockFlag) {
                log.warn("Another person is uploading， MD5 is :{}", dto.getMd5());
                return Mono.just(GlobalResponse.buildFail(ErrorConstants.FILE_IS_UPLOADING_ERROR));
            }
            
            setThreadLocal(dto);
            
            MultiUploadResponseDto result = new MultiUploadResponseDto();
            GlobalResponse<UploadResponseDto> rt = null;
            
            String hashRedisKey = RedisKeyUtils.getBigFileInfoHashKey(dto);
            // if hasKey, this file is not first upload
            if (RedisUtils.hasKey(hashRedisKey)) {
                rt = secondPreUpload(dto, hashRedisKey).block();
            } else {
                rt = initPreUpload(dto, hashRedisKey).block();
            }
            if(Boolean.TRUE.equals(rt.getSuccess())) {
                result.setCompletionRatio(rt.getData().getCompletionRatio());
                result.setSliceSize(rt.getData().getSliceSize());
                
                if(StringUtils.isNotBlank(rt.getData().getS3FilePath())) {
                    result.setS3FilePath(rt.getData().getS3FilePath());
                } else {
                    Map<String, String> values = RedisUtils.hashGetAll(hashRedisKey);
                    Long totalSize = getTotalSizeFromMap(values);
                    Long sliceSize = getSliceSizeFromMap(values);
                    
                    String waitRedisKey = RedisKeyUtils.getWaitSliceKey(dto);
                    Set<String> set = RedisUtils.zGetAll(waitRedisKey);
                    List<SliceInfo> slices = set.stream().parallel().map(item -> {
                        Integer index = Integer.parseInt(item);
                        Long[] split = SliceUtils.getSliceStartAndEnd(sliceSize, totalSize, index);
                        SliceInfo info = new SliceInfo();
                        info.setFileStart(split[0]);
                        info.setFileEnd(split[1]);
                        return info;
                    }).collect(Collectors.toList());
                    
                    result.setSlices(slices);
                }
                return Mono.just(GlobalResponse.buildSuccess(result));
            }
            return Mono.just(GlobalResponse.buildFail(rt.getErrorCode(), rt.getErrorMsg()));
        } catch (InterruptedException e) {
            log.error("redis lock is interrupted", e);
            return Mono.just(GlobalResponse.buildFail(ErrorConstants.FILE_IS_UPLOADING_ERROR));
        } finally {
            if(lockFlag) {
                lock.unlock();
            }
            removeThreadLocal();
        }
    }
    
    
    private void setThreadLocal(BaseDto dto) {
        S3Client s3Client = S3ClientUtils.getClient(dto.getSys());
        clientLocal.set(s3Client);
        S3Config s3Config = S3ClientUtils.getConfig(dto.getSys());
        configLocal.set(s3Config);
    }
    
    private void removeThreadLocal() {
        clientLocal.remove();
        configLocal.remove();
    }

    @Override
    public Mono<GlobalResponse<UploadResponseDto>> postUpload(MultipartFile file, PostUploadRequestDto dto) {
        String hashRedisKey = RedisKeyUtils.getBigFileInfoHashKey(dto);
        Map<String, String> hashValues = RedisUtils.hashGetAll(hashRedisKey);
        
        String md5 = dto.getMd5();
        
        log.info("begin to check upload data， md5:{}", md5);
        ErrorConstants checkResult = checkUpload(file, dto, hashValues);
        if(!ErrorConstants.SUCCESS.equals(checkResult)) {
            log.error("check upload fail, result is:{}", checkResult.getDesc());
            return Mono.just(GlobalResponse.buildFail(checkResult));
        }
        
        long sliceSize = getSliceSizeFromMap(hashValues);
        int sliceIndex = getSliceIndex(dto.getFileStart(), sliceSize);
        
        String waitRedisKey = RedisKeyUtils.getWaitSliceKey(dto);
        Double score = RedisUtils.zScore(waitRedisKey, String.valueOf(sliceIndex));
        if(null == score) {
            log.warn("Another person is uploading， MD5 is :{}, slice index is:{}, return next slice to upload", dto.getMd5(), sliceIndex);
            return uploadNextSlice(waitRedisKey, hashRedisKey, hashValues, sliceIndex);
        }
        
        log.info("check success, begin process upload");
        
        RLock sliceLock = redissonClient.getLock(RedisKeyUtils.getSliceLock(dto, sliceIndex));
        boolean lockFlag = false;
        try {
            lockFlag = sliceLock.tryLock(1, bigfileConfig.getSliceLockTimeOutSecond(), TimeUnit.SECONDS);
            if(!lockFlag) {
                log.warn("Another person is uploading， MD5 is :{}, slice index is:{}", dto.getMd5(), sliceIndex);
                return uploadNextSlice(waitRedisKey, hashRedisKey, hashValues, sliceIndex);
            }
            // double check
            score = RedisUtils.zScore(waitRedisKey, String.valueOf(sliceIndex));
            if(null == score) {
                log.warn("Another person is upload end， MD5 is :{}, slice index is:{}, return next slice to upload", dto.getMd5(), sliceIndex);
                return uploadNextSlice(waitRedisKey, hashRedisKey, hashValues, sliceIndex);
            }
            
            S3Client s3Client = S3ClientUtils.getClient(dto.getSys());
            clientLocal.set(s3Client);
            S3Config s3Config = S3ClientUtils.getConfig(dto.getSys());
            configLocal.set(s3Config);
            
            Long totalSlice = getTotalSliceFromMap(hashValues);
            String uploadId = getS3UploadIdFromMap(hashValues);
            // s3 file path
            String s3File = FileUtils.getS3RealFilePath(dto.getSys(), md5);
            // upload temp file/only one slice file to s3
            String eTag = uploadFileToS3(file, s3File, totalSlice, uploadId, sliceIndex);
            if(StringUtils.isBlank(eTag)) {
                return Mono.just(GlobalResponse.buildFail(ErrorConstants.UPLOAD_S3_ERROR));
            }
            
            if(1 == totalSlice) {
                // only one slice
                return processOnlyOneSlice(hashRedisKey, waitRedisKey, s3File, sliceIndex);
            }
            
            // add to redis uploaded eTag set
            String endRedisKey = RedisKeyUtils.getEndSliceKey(dto);
            
            RedisUtils.getStringRedisTemplate().executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.multi();
                    connection.zAdd(endRedisKey.getBytes(), sliceIndex, eTag.getBytes());
                    connection.expire(endRedisKey.getBytes(), bigfileConfig.getRedisTimeOutSecond());
                    connection.exec();
                    return null;
                }});
            
            long waitSize = RedisUtils.zSize(waitRedisKey);
            if(1 == waitSize) {
                return lastPartUpload(dto.getSys(), hashValues, endRedisKey, uploadId, hashRedisKey, waitRedisKey, sliceIndex);
            }
            // remove redis wait set
            RedisUtils.zRemove(waitRedisKey, String.valueOf(sliceIndex));
            return uploadNextSlice(waitRedisKey, hashRedisKey, hashValues, sliceIndex);
        } catch (InterruptedException e) {
            log.warn("redis lock is interrupted", e);
            return Mono.just(GlobalResponse.buildFail(ErrorConstants.UPLOAD_S3_ERROR));
        } finally {
            if(lockFlag) {
                sliceLock.unlock();
            }
            clientLocal.remove();
            configLocal.remove();
        }
    }
    
    private ErrorConstants checkUpload(MultipartFile file, PostUploadRequestDto dto, Map<String, String> hashValues) {
        if (null==hashValues || hashValues.isEmpty()) {
            log.error("redis key is not exists");
            return ErrorConstants.REDIS_KEY_NOT_EXISTS_ERROR;
        }
        if(dto.getFileEnd().compareTo(dto.getFileStart()) < 0) {
            log.error("fileEnd must more than the fileBegin");
            return ErrorConstants.FILE_END_MUST_MORE_THEN_BEGIN_ERROR;
        }
        
        long sliceSize = getSliceSizeFromMap(hashValues);
        if(dto.getFileStart() % sliceSize != 0) {
            log.error("fileStart is error");
            return ErrorConstants.FILE_BEGIN_ERROR;
        }
        
        long size = dto.getFileEnd()-dto.getFileStart();
        if(size > sliceSize) {
            log.error("fileEnd is error");
            return ErrorConstants.FILE_END_ERROR;
        }
        
        long fileSize = file.getSize();
        if(fileSize != size) {
            log.error("file size  is error");
            return ErrorConstants.FILE_SIZE_ERROR;
        }
        return ErrorConstants.SUCCESS;
    }
    
    private int getSliceIndex(Long fileStart, Long sliceSize) {
        return new Double(fileStart/sliceSize).intValue();
    }
    
    private Mono<GlobalResponse<UploadResponseDto>> uploadNextSlice(String waitRedisKey, String hashRedisKey,  Map<String, String> hashValues, int cuurentSliceIndex) {
        String sliceIndex = RedisUtils.zGetNextValue(waitRedisKey, cuurentSliceIndex);
        if(StringUtils.isBlank(sliceIndex)) {
            String s3File = RedisUtils.hashGet(hashRedisKey, RedisKeyUtils.S3_PATH);
            int count = 0;
            int maxCount = bigfileConfig.getSliceLockTimeOutSecond();
            // max wait 300 Second
            while (StringUtils.isBlank(s3File) && count<maxCount) {
                try {
                    Thread.sleep(1000L);
                    log.info("wait file upload end {} second, redis key:{}", count+1, hashRedisKey);
                } catch (InterruptedException e) {
                }
                s3File = RedisUtils.hashGet(hashRedisKey, RedisKeyUtils.S3_PATH);
                count++;
            }
            if(StringUtils.isBlank(s3File)) {
                return Mono.just(GlobalResponse.buildFail(ErrorConstants.SOMEONE_IS_UPLOADING_SLICE_ERROR));
            }
            return Mono.just(GlobalResponse.buildSuccess(UploadResponseDto.builder().completionRatio(1f).s3FilePath(s3File).build()));
        }
        return getSliceInfo(hashValues, Integer.valueOf(sliceIndex), waitRedisKey);
    }

    private String uploadFileToS3(MultipartFile file, String s3File, Long totalSlice, String uploadId, int sliceIndex) {
        if(1 == totalSlice) {
            return uploadRealFileToS3(file, s3File);
        }
        return uploadPartFileToS3(file, s3File, totalSlice, uploadId, sliceIndex);
    }
    
    private String uploadRealFileToS3(MultipartFile file, String s3File) {
        try (InputStream is = file.getInputStream(); ){
            PutObjectRequest puRequest = PutObjectRequest.builder()
                .bucket(configLocal.get().getBucketName())
                .key(s3File)
                .build();
            RequestBody reqBody = RequestBody.fromInputStream(is, file.getSize());
            PutObjectResponse result = clientLocal.get().putObject(puRequest, reqBody);
            
            if(!Objects.isNull(result)) {
                return result.eTag();
            }
        } catch (Exception e) {
            log.error("upload fail", e);
        }
        return null;
    }
    
    private String uploadPartFileToS3(MultipartFile file, String s3File, Long totalSlice, String uploadId, int sliceIndex) {
        try(InputStream is = file.getInputStream();) {
            UploadPartRequest request = UploadPartRequest.builder()
                .bucket(configLocal.get().getBucketName())
                .key(s3File)
                .partNumber(sliceIndex+1)
                .uploadId(uploadId)
                .build();
            
            UploadPartResponse result = clientLocal.get().uploadPart(request, RequestBody.fromInputStream(is, file.getSize()));
            if(!Objects.isNull(result)) {
                return result.eTag();
            }
        } catch (Exception e) {
            log.error("upload fail", e);
        }
        return null;
    }
    
    private Mono<GlobalResponse<UploadResponseDto>> processOnlyOneSlice(String hashRedisKey, String waitRedisKey, String s3File, Integer sliceIndex) {
        RedisUtils.getStringRedisTemplate().executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.multi();
                connection.hSet(hashRedisKey.getBytes(), RedisKeyUtils.S3_PATH.getBytes(), s3File.getBytes());
                connection.zRem(waitRedisKey.getBytes(), String.valueOf(sliceIndex).getBytes());
                connection.exec();
                return null;
            }});
        UploadResponseDto respDto = UploadResponseDto.builder()
            .completionRatio(1f)
            .s3FilePath(s3File)
            .build();
        return Mono.just(GlobalResponse.buildSuccess(respDto));
    }
    
    private Mono<GlobalResponse<UploadResponseDto>> lastPartUpload(String sys, Map<String, String> hashValues, String endRedisKey, String uploadId, String hashRedisKey, String waitRedisKey, int sliceIndex) {
        String s3RealFile = FileUtils.getS3RealFilePath(sys, getMd5FromMap(hashValues));
        // the file upload end
        Set<String> endSliceSet = RedisUtils.zGetAll(endRedisKey);
        int totalSlice = getTotalSliceFromMap(hashValues).intValue();
        
        List<CompletedPart> parts = new ArrayList<>(totalSlice);
        ForEachUtils.forEach(0, endSliceSet,(index, item) -> {
            CompletedPart part = CompletedPart.builder().partNumber(index+1).eTag(item).build();
            parts.add(part);
        });
        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder().parts(parts).build();
        CompleteMultipartUploadRequest request = CompleteMultipartUploadRequest.builder()
            .bucket(configLocal.get().getBucketName())
            .key(s3RealFile)
            .uploadId(uploadId)
            .multipartUpload(completedMultipartUpload)
            .build();
        try {
            CompleteMultipartUploadResponse result = clientLocal.get().completeMultipartUpload(request);
            if(Objects.isNull(result) || StringUtils.isBlank(result.eTag())) {
                return Mono.just(GlobalResponse.buildFail(ErrorConstants.COMPLETE_S3_ERROR));
            }
        } catch (Exception e) {
            log.error("s3 complete file error", e);
            return Mono.just(GlobalResponse.buildFail(ErrorConstants.COMPLETE_S3_ERROR));
        }
        
        RedisUtils.getStringRedisTemplate().executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.multi();
                connection.hSet(hashRedisKey.getBytes(), RedisKeyUtils.S3_PATH.getBytes(), s3RealFile.getBytes());
                connection.del(waitRedisKey.getBytes(), endRedisKey.getBytes());
                connection.exec();
                return null;
            }});
        return Mono.just(GlobalResponse.buildSuccess(UploadResponseDto.builder().completionRatio(1f).s3FilePath(s3RealFile).build()));
    }
    
    private Mono<GlobalResponse<UploadResponseDto>> secondPreUpload(PreUploadRequestDto dto, String hashRedisKey) {
        Map<String, String> values = RedisUtils.hashGetAll(hashRedisKey);
        String s3FilePath = getS3FileFromMap(values);
        
        // upload is end
        if(StringUtils.isNotBlank(s3FilePath)) {
            return Mono.just(GlobalResponse.buildSuccess(UploadResponseDto.builder().completionRatio(1f).s3FilePath(s3FilePath).build()));
        }
        
        String waitRedisKey = RedisKeyUtils.getWaitSliceKey(dto);
        String sliceIndex = RedisUtils.zGetFirst(waitRedisKey);
        if(Objects.isNull(sliceIndex)) {
            log.warn("the last slice is uploading by someone, md5:[{}]", dto.getMd5());
            return Mono.just(GlobalResponse.buildFail(ErrorConstants.SOMEONE_IS_UPLOADING_SLICE_ERROR));
        }

        Integer cuurentSlice = Integer.valueOf(sliceIndex);
        
        return getSliceInfo(values, cuurentSlice, waitRedisKey);
    }
    
    private Mono<GlobalResponse<UploadResponseDto>> initPreUpload(PreUploadRequestDto dto, String hashRedisKey) {
        Long sliceSize = bigfileConfig.getSliceSize();
        int totalSlice = SliceUtils.getTotalSlice(sliceSize, dto.getTotalSize());
        
        String uploadId = null;
        if(totalSlice > 1) {
            try {
                String realKey = FileUtils.getS3RealFilePath(dto.getSys(), dto.getMd5());
                CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                    .bucket(configLocal.get().getBucketName()).key(realKey)
                    .build();
                CreateMultipartUploadResponse initResp  = clientLocal.get().createMultipartUpload(createMultipartUploadRequest);
                if(null == initResp  || StringUtils.isBlank(initResp.uploadId())) {
                    return Mono.just(GlobalResponse.buildFail(ErrorConstants.INIT_S3_UPLOAD_ERROR));
                }
                uploadId = initResp.uploadId();
            } catch (Exception e) {
                log.error("init s3 upload error", e);
                return Mono.just(GlobalResponse.buildFail(ErrorConstants.INIT_S3_UPLOAD_ERROR));
            }
        }
        
        Long[] split = SliceUtils.getSliceStartAndEnd(sliceSize, dto.getTotalSize(), 0);
        
        Set<RedisZSetCommands.Tuple> tuples = new HashSet<>(totalSlice);
        for(int i=0; i<totalSlice; i++) {
            RedisZSetCommands.Tuple val = new DefaultTuple(String.valueOf(i).getBytes(), Double.valueOf(i));
            tuples.add(val);
        }
        
        Map<byte[], byte[]> map = new HashMap<>();
        map.put(RedisKeyUtils.FILE_NAME.getBytes(), dto.getFileName().getBytes());
        map.put(RedisKeyUtils.BEGIN_DATE.getBytes(), DateUtils.formatDate(new Date(), DateUtils.Pattern.Date.YYYYMMDD).getBytes());
        map.put(RedisKeyUtils.SLICE_SIZE.getBytes(), String.valueOf(sliceSize).getBytes());
        map.put(RedisKeyUtils.TOTAL_SIZE.getBytes(), String.valueOf(dto.getTotalSize()).getBytes());
        map.put(RedisKeyUtils.TOTAL_SLICE.getBytes(), String.valueOf(totalSlice).getBytes());
        map.put(RedisKeyUtils.MD5.getBytes(), dto.getMd5().getBytes());
        if(totalSlice > 1) {
            map.put(RedisKeyUtils.S3_UPLOAD_ID.getBytes(), uploadId.getBytes());
        }
        
        String waitRedisKey = RedisKeyUtils.getWaitSliceKey(dto);
        
        RedisUtils.getStringRedisTemplate().executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.multi();
                connection.zAdd(waitRedisKey.getBytes(), tuples);
                connection.expire(waitRedisKey.getBytes(), bigfileConfig.getRedisTimeOutSecond());
                
                connection.hashCommands().hMSet(hashRedisKey.getBytes(), map);
                connection.expire(hashRedisKey.getBytes(), bigfileConfig.getRedisTimeOutSecond());
                connection.exec();
                return null;
            }});
        
        UploadResponseDto respDto = UploadResponseDto.builder().fileStart(split[0]).fileEnd(split[1]).sliceSize(bigfileConfig.getSliceSize()).completionRatio(0f).build();
        return Mono.just(GlobalResponse.buildSuccess(respDto));
    }
    
    private Mono<GlobalResponse<UploadResponseDto>> getSliceInfo(Map<String, String> values, Integer cuurentSlice, String waitRedisKey) {
        Long totalSize = getTotalSizeFromMap(values);
        Long sliceSize = getSliceSizeFromMap(values);
        Long totalSlice = getTotalSliceFromMap(values);
        Long waitSlice = RedisUtils.zSize(waitRedisKey);
        
        Long[] split = SliceUtils.getSliceStartAndEnd(sliceSize, totalSize, cuurentSlice);
        float completionRatio = SliceUtils.getCompletionRatio(totalSlice, waitSlice);
        
        UploadResponseDto respDto = UploadResponseDto.builder().fileStart(split[0]).fileEnd(split[1]).sliceSize(sliceSize).completionRatio(completionRatio).build();
        return Mono.just(GlobalResponse.buildSuccess(respDto));
    }
    
    private Long getTotalSliceFromMap(Map<String, String> hashValues) {
        return Long.valueOf(hashValues.get(RedisKeyUtils.TOTAL_SLICE));
    }
    
    private Long getTotalSizeFromMap(Map<String, String> hashValues) {
        return Long.valueOf(hashValues.get(RedisKeyUtils.TOTAL_SIZE));
    }
    
    private Long getSliceSizeFromMap(Map<String, String> hashValues) {
        return Long.valueOf(hashValues.get(RedisKeyUtils.SLICE_SIZE));
    }
    
    private String getMd5FromMap(Map<String, String> hashValues) {
        return hashValues.get(RedisKeyUtils.MD5);
    }
    
    private String getS3FileFromMap(Map<String, String> hashValues) {
        return hashValues.get(RedisKeyUtils.S3_PATH);
    }
    
    private String getS3UploadIdFromMap(Map<String, String> hashValues) {
        return hashValues.get(RedisKeyUtils.S3_UPLOAD_ID);
    }
}
