package com.nike.gcsc.fileupload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import com.nike.gcsc.fileupload.config.RedisConfiguration;

@SpringBootApplication
@EnableDiscoveryClient
@Import(RedisConfiguration.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
