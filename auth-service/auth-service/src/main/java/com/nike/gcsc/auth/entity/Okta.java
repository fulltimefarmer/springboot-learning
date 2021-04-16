package com.nike.gcsc.auth.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "okta")
public class Okta implements Serializable {

	private static final long serialVersionUID = 7291439541350402789L;

	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String appName;

    @Column(nullable = false, unique = true)
    private String clientId;

    private String owner;

    private String status;

    private String secret;

    private String redirectUrl;

    private String customScopes;

    private String applicationType;

    private String grantTypes;

    private Long projectId;

    @Column(name = "[describe]")
    private String describe;
}
