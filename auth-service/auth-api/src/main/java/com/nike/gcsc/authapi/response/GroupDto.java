package com.nike.gcsc.authapi.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class GroupDto implements Serializable {

	private static final long serialVersionUID = 2136260393523137899L;
	
	private Long id;
    private String name;
    private String remark;
    
}
