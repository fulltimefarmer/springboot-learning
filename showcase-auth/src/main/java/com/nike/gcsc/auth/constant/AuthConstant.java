package com.nike.gcsc.auth.constant;

public class AuthConstant {

    //grant type
    public static final String OKTA_AUTHORIZATION_CODE = "authorization_code";
    public static final String OKTA_CLIENT_CREDENTIALS = "client_credentials";

    //request params
    public static final String OKTA_CONTENT_TYPE = "Content-Type";
    public static final String OKTA_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    public static final String OKTA_CLIENT_ID = "client_id";
    public static final String OKTA_CLIENT_SECRET = "client_secret";
    public static final String OKTA_GRANT_TYPE = "grant_type";
    public static final String OKTA_CODE = "code";
    public static final String OKTA_REDIRECT_URI = "redirect_uri";
    public static final String OKTA_TOKEN="token";

    //token prefix
    public static final String TOKEN_REDIS_PREFIX = "AUTH:";

}
