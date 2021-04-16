package com.nike.gcsc.authapi.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PermissionRequestBean implements Serializable {

	private static final long serialVersionUID = -3650240441516762817L;

    private String name;
    private String method;
    private int type;
    private String uriRegPattern;
    private String remark;
    private Long projectId;
    
}
