package com.nike.gcsc.auth.model.okta;

import lombok.Data;

import java.io.Serializable;

@Data
public class OktaUserinfo implements Serializable {

	private static final long serialVersionUID = -5374541380612348636L;
	
	private String sub;
	private String name;
	private String locale;
	private String email;
	private String preferredUsername;
	private String givenName;
	private String familyName;
	private String zoneinfo;
	private Long updatedAt;
	private Boolean emailVerified;
	private OktaAddress address;
	
}
