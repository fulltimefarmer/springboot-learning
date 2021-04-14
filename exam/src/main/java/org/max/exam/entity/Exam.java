package org.max.exam.entity;

import java.io.Serializable;

public class Exam implements Serializable {

	private static final long serialVersionUID = -8332719597645165088L;

    private Long id;
    private String username;
    private String score;
    
	public Exam() {
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

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}
}