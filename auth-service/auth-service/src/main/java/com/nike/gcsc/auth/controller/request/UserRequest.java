package com.nike.gcsc.auth.controller.request;

import lombok.Data;

import java.util.Date;

@Data
public class UserRequest {

    private Long id;
    private String username;
    private String password;
    private String displayName;
    private String email;
    private Integer type;
    private Date createDate;
    private Date lastModifyDate;
    private Integer status;
}
