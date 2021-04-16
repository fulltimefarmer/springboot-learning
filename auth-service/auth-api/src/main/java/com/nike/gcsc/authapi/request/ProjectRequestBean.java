package com.nike.gcsc.authapi.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectRequestBean implements Serializable {

	private static final long serialVersionUID = 9104864727960041904L;
	
	private String projectName;
    private String name;
    private String description;

}
