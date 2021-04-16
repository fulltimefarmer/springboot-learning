package com.nike.gcsc.authapi.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class PermissionDto implements Serializable {

	private static final long serialVersionUID = -3368241264661897986L;
	
	private Long id;
    private String name;
    private int type;
    private String method;
    private String uriRegPattern;
    private String remark;

}
