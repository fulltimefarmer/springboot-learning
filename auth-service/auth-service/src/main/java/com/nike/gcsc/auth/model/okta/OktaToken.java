package com.nike.gcsc.auth.model.okta;

import lombok.Data;

import java.io.Serializable;

@Data
public class OktaToken implements Serializable {
	
	private static final long serialVersionUID = 1068067919022680709L;
	
	private String tokenType;
	private int expiresIn;
	private String accessToken;
	private String scope;
	private String idToken;
	
}
