package org.max.learning.common.controller;


import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/test")
public class TestController {
	
	@ApiOperation(value = "index", notes = "index")
    @GetMapping("/index")
    public ResponseEntity<String> index(
            HttpServletRequest req,
            HttpServletResponse resp) {
		log.trace("This trace log.");
        log.debug("This debug log.");
        log.info("This info log.");
        log.warn("This warn log.");
        log.error("This error log.");
		return new ResponseEntity<String>("index", HttpStatus.OK);
    }
	
    @ApiOperation(value = "test get", notes = "test get")
    @GetMapping("/test_get")
    public ResponseEntity<String> listAll(
            HttpServletRequest req,
            HttpServletResponse resp) {
        String result = "TEST GET IN POC.";
        
        String uri = req.getRequestURI();
        System.out.println("URI: " + uri);
        StringBuffer url = req.getRequestURL();
        System.out.println("URL: " + url.toString());
        
        System.out.println("============ headers ===========");
        Enumeration<String> headerKeys = req.getHeaderNames();
        while(headerKeys.hasMoreElements()) {
        	String key = headerKeys.nextElement();
        	String value = req.getHeader(key);
        	System.out.println(key + ":" + value);
        }
        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

}

