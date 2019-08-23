package com.nike.gcsc.auth.dto;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "okta")
public class Okta implements Serializable {

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

}
