package com.nike.gcsc.authapi.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class OktaDto implements Serializable {

	private static final long serialVersionUID = 8148436241067873418L;
	
	private Long id;
    private String appName;
    private String applicationType;
    private String clientId;
    private String owner;
    private String status;
    private String secret;
    private String redirectUrl;
    private String customScopes;
    private String grantTypes;
    private String projectName;
    private Long projectId;
    private String describe;
}
