package com.nike.gcsc.auth.model.okta;

import lombok.Data;

import java.io.Serializable;

@Data
public class OktaIntrospect implements Serializable {

	private Boolean active;
	private String scope;
	private String username;
	private Long exp;
	private Long iat;
	private String sub;
	private String aud;
	private String iss;
	private String jti;
	private String tokenType;
	private String clientId;
	private String uid;

}
