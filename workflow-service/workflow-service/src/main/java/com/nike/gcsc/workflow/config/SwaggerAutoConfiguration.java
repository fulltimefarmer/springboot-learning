package com.nike.gcsc.workflow.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/** 
 * @author ezha42
 * @date 2019-06-16
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = "swagger",name="enabled",havingValue ="true")
public class SwaggerAutoConfiguration {

    @Bean
    public Docket openDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(openApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.nike.gcsc.workflow.service"))
                .paths(PathSelectors.any())
                .build();
    }
    
    @Bean
    public Docket backDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(backApiInfo())
                .groupName("Rest for Mannager")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.nike.gcsc.workflow.rest"))
                .paths(PathSelectors.any())
                .build();
    }
    
    private ApiInfo openApiInfo() {
        return new ApiInfoBuilder()
            .title("workflow-service open Api")
            .description("workflow-service open Api")
            .version("1.0")
            .build();
    }

    private ApiInfo backApiInfo() {
        return new ApiInfoBuilder()
                .title("workflow-service background Api")
                .description("workflow-service background Api")
                .version("1.0")
                .build();
    }
}