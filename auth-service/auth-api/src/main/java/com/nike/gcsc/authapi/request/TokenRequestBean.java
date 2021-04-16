package com.nike.gcsc.authapi.request;

import lombok.Data;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

@Data
public class TokenRequestBean implements Serializable {
	
	private static final long serialVersionUID = 6713760360328641568L;

	@NotBlank
	private String token;
	
}
