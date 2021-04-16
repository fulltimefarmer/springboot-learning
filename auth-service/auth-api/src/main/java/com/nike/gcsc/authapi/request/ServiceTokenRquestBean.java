 package com.nike.gcsc.authapi.request;

import lombok.Setter;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author roger yang
 * @date 9/19/2019
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTokenRquestBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -8117041817343976918L;
    /**
     * okta token
     */
    @NotBlank
    private String token;
    /**
     * okta client id
     */
    @NotBlank
    private String clientId;
    
    @NotNull
    private Boolean isAuthToken;
}
