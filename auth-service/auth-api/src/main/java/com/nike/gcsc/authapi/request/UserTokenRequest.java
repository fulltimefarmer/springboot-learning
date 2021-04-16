package com.nike.gcsc.authapi.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: tom fang
 * @date: 2019/8/20 15:32
 **/
@Data
public class UserTokenRequest implements Serializable {
	
	private static final long serialVersionUID = 8979852718405471211L;
	
	private String username;
    private String password;
    private Long projectId;
}
