package com.nike.gcsc.auth.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "gcsc_token")
public class Token implements Serializable {

	private static final long serialVersionUID = 5105935860292138707L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String token;

	@Column(nullable = false)
	private String username;

	@Column(name="project_id")
	private Long projectId;

	@Column(name = "create_time", nullable = false)
	private Long createTime;

	@Column(name = "expire_time", nullable = false)
	private Long expireTime;

}
