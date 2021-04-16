package com.nike.gcsc.auth.utils;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;

import com.nike.gcsc.auth.model.okta.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtils {

    private static SecretKey secretKey = new SecretKeySpec("1d8aed0df761c7e".getBytes(),"AES");

    /**
     * Create Token by Jwt
     * @param jwt
     * @return  String
     *
     */
    public static String createToken(Jwt jwt) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        JwtBuilder builder = Jwts.builder()
                .setIssuer(jwt.getIss())
                .setSubject(jwt.getSub())
                .setAudience(jwt.getAud())
                .setExpiration(new Date(jwt.getExp()*1000))
                .setNotBefore(new Date(jwt.getNbf()))
                .setIssuedAt(new Date(jwt.getIat()))
                .setId(jwt.getJti())
                .signWith(signatureAlgorithm, secretKey);
        return builder.compact();
    }
    
    /**
     * 
     * Parse Jwt by Token
     * @param token
     * @return
     *
     */
    public static Jwt parseToken(String token) {
        try {
            Jwt jwt = new Jwt();
		    Claims claims =Jwts.parser()
		        .setSigningKey(secretKey)
		        .parseClaimsJws(token)
		        .getBody();
		    jwt.setIss(claims.getIssuer());
		    jwt.setSub(claims.getSubject());
		    jwt.setAud(claims.getAudience());
		    jwt.setExp(claims.getExpiration().getTime());
		    jwt.setNbf(claims.getNotBefore().getTime());
		    jwt.setIat(claims.getIssuedAt().getTime());
		    jwt.setJti(claims.getId());
		    return jwt;
	    } catch (Exception e) {
		    log.error("JwtUtils:parseToken:"+ token, e);
		    return null;
	    }
    }

    /**
     *
     * verify Jwt
     * @param jwt
     * @return Boolean
     *
     */
    public static Boolean verifyJwt(Jwt jwt) {
        if(null == jwt){
            return Boolean.FALSE;
        }
        if(StringUtils.isBlank(jwt.getSub())){
            return Boolean.FALSE;
        }
        Long now = Instant.now().getEpochSecond();
        if(now > jwt.getExp()){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}