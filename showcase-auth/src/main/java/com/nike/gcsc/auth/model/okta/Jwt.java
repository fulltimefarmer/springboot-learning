package com.nike.gcsc.auth.model.okta;

import lombok.Data;

import java.io.Serializable;

@Data
public class Jwt implements Serializable {

    private String iss;//issuer
    private String sub;//username
    private String aud;//
    private long exp;//expired time
    private long nbf;//not active before
    private long iat;//issue time
    private String jti;//jwt id

}
