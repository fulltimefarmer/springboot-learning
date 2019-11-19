package org.max.learning.common.controller;


import java.util.Enumeration;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
    @ApiOperation(value = "header", notes = "header")
    @GetMapping("/header")
    public ResponseEntity<String> header(
            HttpServletRequest req,
            HttpServletResponse resp) {
        StringBuilder sb = new StringBuilder();
        sb.append("URI:").append(req.getRequestURI()).append("\n");
        sb.append("URL:").append(req.getRequestURL()).append("\n");
        sb.append("============ headers ===========");
        Enumeration<String> headerKeys = req.getHeaderNames();
        while(headerKeys.hasMoreElements()) {
        	String key = headerKeys.nextElement();
        	String value = req.getHeader(key);
        	sb.append(key).append(":").append(value).append("\n");
        }
        return new ResponseEntity<String>(sb.toString(), HttpStatus.OK);
    }

    @ApiOperation(value = "attribute", notes = "attribute")
    @GetMapping("/attribute")
    public ResponseEntity<String> attribute(
            HttpServletRequest req,
            HttpServletResponse resp) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> attributeKeys = req.getAttributeNames();
        while(attributeKeys.hasMoreElements()) {
        	String key = attributeKeys.nextElement();
        	Object value = req.getAttribute(key);
        	sb.append(key).append(":").append(value.toString()).append("\n");
        }
        return new ResponseEntity<String>(sb.toString(), HttpStatus.OK);
    }
    
    @ApiOperation(value = "param", notes = "param")
    @GetMapping("/params")
    public ResponseEntity<String> param(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestParam(name = "params") Set<String> params) {
    	StringBuilder sb = new StringBuilder();
    	for(String str : params) {
    		System.out.println(str);
    		sb.append(str).append(";");
    	}
        return new ResponseEntity<String>(sb.toString(), HttpStatus.OK);
    }
}

