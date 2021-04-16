package com.nike.gcsc.auth.model.okta;

import lombok.Data;

import java.io.Serializable;

@Data
public class OktaIntrospect implements Serializable {

	private static final long serialVersionUID = -7324260819432727410L;
	
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
