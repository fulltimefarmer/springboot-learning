package com.nike.gcsc.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate autoconfig 
 * @author roger yang
 * @date 9/10/2019
 */
@Configuration
public class RestClientConfig {

    @Bean
    public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(60000);
        factory.setConnectTimeout(90000);
        return factory;
    }

    @Bean
    public RestTemplate restTemplate(SimpleClientHttpRequestFactory factory){
        return new RestTemplate(factory);
    }

}