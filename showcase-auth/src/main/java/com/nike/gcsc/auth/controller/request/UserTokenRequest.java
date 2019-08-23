package com.nike.gcsc.auth.controller.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: tom fang
 * @date: 2019/8/20 15:32
 **/
@Data
public class UserTokenRequest implements Serializable {
    private String username;
    private String password;
    private Long projectId;
}
