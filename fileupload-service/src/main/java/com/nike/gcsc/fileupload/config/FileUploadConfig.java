 package com.nike.gcsc.fileupload.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roger yang
 * @date 11/04/2019
 */
@Configuration
@ConfigurationProperties(prefix = "fileupload")
@Getter
@Setter
public class FileUploadConfig {
    private Long sliceSize;
    
    private String tempPath;
    
    private Long redisTimeOutSecond;
    
    private Integer fileLockTimeOutSecond;
    
    private Integer sliceLockTimeOutSecond;
}
