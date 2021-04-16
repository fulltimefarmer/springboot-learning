 package com.nike.gcsc.fileupload.service;

import org.springframework.web.multipart.MultipartFile;

import com.nike.gcsc.common.GlobalResponse;
import com.nike.gcsc.fileupload.dto.request.PostUploadRequestDto;
import com.nike.gcsc.fileupload.dto.request.PreUploadRequestDto;
import com.nike.gcsc.fileupload.dto.response.MultiUploadResponseDto;
import com.nike.gcsc.fileupload.dto.response.UploadResponseDto;

import reactor.core.publisher.Mono;

/**
 * @author roger yang
 * @date 11/05/2019
 */
public interface ProcessService {
    Mono<GlobalResponse<UploadResponseDto>> preUpload(PreUploadRequestDto dto) ;
    
    Mono<GlobalResponse<MultiUploadResponseDto>> preMultiUpload(PreUploadRequestDto dto) ;

    Mono<GlobalResponse<UploadResponseDto>> postUpload(MultipartFile file, PostUploadRequestDto dto) ;
}
