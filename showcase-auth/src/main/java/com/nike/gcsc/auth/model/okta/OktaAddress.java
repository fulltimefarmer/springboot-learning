package com.nike.gcsc.auth.model.okta;

import lombok.Data;

import java.io.Serializable;

@Data
public class OktaAddress implements Serializable {

	private String streetAddress;
	private String locality;
	private String region;
	private String postalCode;
	private String country;
	
}
