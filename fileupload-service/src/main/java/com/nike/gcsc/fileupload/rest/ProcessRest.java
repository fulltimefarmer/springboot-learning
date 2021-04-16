 package com.nike.gcsc.fileupload.rest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.nike.gcsc.common.GlobalResponse;
import com.nike.gcsc.fileupload.dto.request.PostUploadRequestDto;
import com.nike.gcsc.fileupload.dto.request.PreUploadRequestDto;
import com.nike.gcsc.fileupload.dto.response.MultiUploadResponseDto;
import com.nike.gcsc.fileupload.dto.response.UploadResponseDto;
import com.nike.gcsc.fileupload.service.ProcessService;
import com.nike.gcsc.fileupload.util.RedisKeyUtils;
import com.nike.gcsc.fileupload.util.RedisUtils;
import com.nike.gcsc.fileupload.util.S3ClientUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

/**
 * @author roger yang
 * @date 10/31/2019
 */
@RestController
@RequestMapping("/v1/api")
@Slf4j
public class ProcessRest {
    @Autowired
    private ProcessService processService;
    
    @PostMapping("/pre")
    public Mono<GlobalResponse<UploadResponseDto>> preUpload(@RequestBody @Valid PreUploadRequestDto dto) {
        log.info("begin call preUpload, request is : {}", JSONObject.toJSONString(dto));
        
        Mono<GlobalResponse<UploadResponseDto>> resp = processService.preUpload(dto);
        
        log.info("end call preUpload, response is : {}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    @PostMapping("/pre_multi")
    public Mono<GlobalResponse<MultiUploadResponseDto>> preMultiUpload(@RequestBody @Valid PreUploadRequestDto dto) {
        log.info("begin call preUpload, request is : {}", JSONObject.toJSONString(dto));
        
        Mono<GlobalResponse<MultiUploadResponseDto>> resp = processService.preMultiUpload(dto);
        
        log.info("end call preUpload, response is : {}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    @PostMapping(value="/upload", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<GlobalResponse<UploadResponseDto>> postUpload(@RequestParam(value="file", required = true) MultipartFile file, @Valid PostUploadRequestDto dto) {
        log.info("begin to upload file, request is:{}", JSONObject.toJSONString(dto));
        
        Mono<GlobalResponse<UploadResponseDto>> resp = processService.postUpload(file, dto);
        
        log.info("end upload file, response is:{}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    /**
     * 
     * @param file e.g: cig/dev/fadfadfad
     * @param response
     * @throws Exception
     */
    @GetMapping("/download")
    public void download(@RequestParam String file, HttpServletResponse response) throws Exception {
        if(StringUtils.isBlank(file)) {
            response.setStatus(HttpStatus.NOT_FOUND.value()); 
            return;
        }
        String[] arr = file.split("/");
        if(arr.length != 3) {
            response.setStatus(HttpStatus.NOT_FOUND.value()); 
            return;
        }
        String sys = arr[0];
        String md5 = arr[2];
        String fileName = RedisUtils.hashGet(RedisKeyUtils.getBigFileInfoHashKey(sys, md5), RedisKeyUtils.FILE_NAME);
        if(StringUtils.isBlank(fileName)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        
        fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        GetObjectRequest objRequest = GetObjectRequest.builder().bucket(S3ClientUtils.getConfig(sys).getBucketName()).key(file).build();
        try(ServletOutputStream out = response.getOutputStream(); 
            ResponseInputStream<GetObjectResponse> is = S3ClientUtils.getClient(sys).getObject(objRequest);) {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition,Content-Length");
            
            byte[] buffer = new byte[1024];
            
            int i = is.read(buffer);
            while(i != -1) {
                out.write(buffer, 0, i);
                i = is.read(buffer);
            }
            is.close();
            out.flush();
        }
    }
}
