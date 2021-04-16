 package com.nike.gcsc.fileupload.dto.request;

import javax.validation.constraints.NotBlank;

import com.nike.gcsc.fileupload.constant.SysConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roger yang
 * @date 11/04/2019
 */
@Getter
@Setter
public abstract class BaseDto {
    /**
     * business system
     * {@link SysConstants}
     */
    @NotBlank
    private String sys;
    
    /**
     * file md5
     */
    @NotBlank
    private String md5;
}
