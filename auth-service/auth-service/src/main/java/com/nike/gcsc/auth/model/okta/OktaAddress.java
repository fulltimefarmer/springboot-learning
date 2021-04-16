package com.nike.gcsc.auth.model.okta;

import lombok.Data;

import java.io.Serializable;

@Data
public class OktaAddress implements Serializable {

	private static final long serialVersionUID = -3355149407249654312L;
	
	private String streetAddress;
	private String locality;
	private String region;
	private String postalCode;
	private String country;
	
}
