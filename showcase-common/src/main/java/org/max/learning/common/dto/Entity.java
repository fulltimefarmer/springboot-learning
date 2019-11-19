package org.max.learning.common.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class Entity implements Serializable {

	private static final long serialVersionUID = -4449865512934528938L;
	
	private String key;
	private String value;
	
}
