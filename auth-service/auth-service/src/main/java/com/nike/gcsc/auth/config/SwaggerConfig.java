package com.nike.gcsc.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = "swagger",name="enabled",havingValue ="true")
public class SwaggerConfig {

	@Bean
	public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
				.useDefaultResponseMessages(Boolean.FALSE)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.nike.gcsc.auth.controller"))
                .paths(PathSelectors.any())
                .build();
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
                .title("Auth Service APIs")
				.contact(new Contact("Max", "", "max.zhou@nike.com"))
                .description("Auth Service APIs")
                .version("1.0")
                .build();
	}
	
}
