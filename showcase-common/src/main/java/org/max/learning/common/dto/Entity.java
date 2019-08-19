package org.max.learning.common.dto;

import java.util.List;

import lombok.Data;

@Data
public class Entity {

	private String key;
	private String value;
	private List<User> userList;
	
}
