 package com.nike.gcsc.authapi.response;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roger yang
 * @date 12/03/2019
 */
@Getter
@Setter
public class ChangePwdDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4424683396061561979L;
    
    private Long userId;
    
    private String username;
    
    @NotBlank
    private String password;
}
