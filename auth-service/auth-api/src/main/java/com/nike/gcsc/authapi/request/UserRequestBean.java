package com.nike.gcsc.authapi.request;

import lombok.Data;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
/**
 * @author: tom fang
 * @description:
 * @date: 13:40 2019/7/5
 **/
@Data
public class UserRequestBean implements Serializable {

	private static final long serialVersionUID = -3650240441516762817L;
	
	@NotBlank
    private String username;
	@NotBlank
    private String displayName;
	@NotBlank
    private String email;
    private String remark;
}
