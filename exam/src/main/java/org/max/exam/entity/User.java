package org.max.exam.entity;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = -8319205747216528962L;
	
	private Long id;
    private String username;
    private String password;
    private String role;

	public User() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
