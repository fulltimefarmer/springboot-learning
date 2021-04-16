package com.nike.gcsc.auth.model.okta;

import lombok.Data;

import java.io.Serializable;

@Data
public class Jwt implements Serializable {

	private static final long serialVersionUID = -416568811008876638L;
	
	private String iss;
    private String sub;
    private String aud;
    private long exp;
    private long nbf;
    private long iat;
    private String jti;

}
