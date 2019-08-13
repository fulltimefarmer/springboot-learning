package org.max.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@ConditionalOnProperty(prefix = "custom.cors",name="enable",havingValue ="true")
public class WebFluxConfig implements WebFluxConfigurer {

    @Value("${custom.cors.mapping}")
    public String mapping;
    @Value("${custom.cors.allowCredentials}")
    public boolean allowCredentials;
    @Value("${custom.cors.allowedOrigins}")
    public String allowedOrigins;
    @Value("${custom.cors.allowedHeaders}")
    public String allowedHeaders;
    @Value("${custom.cors.allowedMethods}")
    public String allowedMethods;
    @Value("${custom.cors.maxAge}")
    public long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(mapping)
                .allowCredentials(allowCredentials)
                .allowedOrigins(allowedOrigins)
                .allowedHeaders(allowedHeaders)
                .allowedMethods(allowedMethods)
                .maxAge(maxAge)
                .exposedHeaders(HttpHeaders.SET_COOKIE);
    }

    @Bean
    CorsWebFilter corsWebFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(allowCredentials);
        corsConfiguration.addAllowedHeader(allowedHeaders);
        corsConfiguration.addAllowedMethod(allowedMethods);
        corsConfiguration.addAllowedOrigin(allowedOrigins);
        corsConfiguration.setMaxAge(maxAge);
        corsConfiguration.addExposedHeader(HttpHeaders.SET_COOKIE);
        CorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        ((UrlBasedCorsConfigurationSource) corsConfigurationSource).registerCorsConfiguration(mapping, corsConfiguration);
        return new CorsWebFilter(corsConfigurationSource);
    }

}
