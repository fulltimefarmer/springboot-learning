package com.nike.gcsc.authapi.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class OktaRequestBean implements Serializable {

	private static final long serialVersionUID = -3650240441516762817L;
	
    private String projectName;
    private String appName;
    private String applicationType;
    private String clientId;
    private String owner;
    private String status;
    private String secret;
    private String redirectUrl;
    private String customScopes;
    private String grantTypes;
    private Long projectId;
    private String describe;
}
