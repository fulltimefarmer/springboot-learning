package com.nike.gcsc.authapi.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceTokenRequest implements Serializable {
	
	/**
     *
     */
    private static final long serialVersionUID = -7617242423908456290L;
    
    private String clientId;
    private String secret;

}
