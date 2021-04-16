package com.nike.gcsc.authapi.request;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccessRequestBean implements Serializable {

	private static final long serialVersionUID = 4867416936868652932L;
	
	private String token;
	private String clientId;
	private String method;
	private String uri;
	
}
