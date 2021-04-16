package com.nike.gcsc.authapi.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectDto implements Serializable {

	private static final long serialVersionUID = 9104864727960041904L;
	
	private Long id;
    private String name;
    private String description;

}
