package com.nike.gcsc.auth.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

	@ApiOperation(value = "health check", notes = "health check")
    @GetMapping(value = "/public/health_check")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("ALIVE", HttpStatus.OK);
    }
	
    @ApiOperation(value = "greeting", notes = "greeting")
    @GetMapping("/service/greeting")
    public ResponseEntity<String> greeting() {
        return new ResponseEntity<>("AUTH SERVICE GET METHOD", HttpStatus.OK);
    }

}
