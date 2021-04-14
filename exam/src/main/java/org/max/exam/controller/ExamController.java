package org.max.exam.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/test")
public class ExamController {

	@ApiOperation(value = "health check", notes = "health check")
    @GetMapping(value = "/public/health_check")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("ALIVE", HttpStatus.OK);
    }
	
    @ApiOperation(value = "greeting", notes = "greeting")
    @GetMapping("/api/greeting")
    public ResponseEntity<String> greeting() {
        return new ResponseEntity<>("AUTH SERVICE GET METHOD", HttpStatus.OK);
    }
    
    @ApiOperation(value = "greeting", notes = "greeting")
    @DeleteMapping("/api/greeting")
    public ResponseEntity<String> testDelete() {
    	return new ResponseEntity<>("AUTH SERVICE DELETE METHOD", HttpStatus.OK);
    }
    
    @ApiOperation(value = "greeting", notes = "greeting")
    @PostMapping("/api/greeting")
    public ResponseEntity<String> testPost() {
    	return new ResponseEntity<>("AUTH SERVICE POST METHOD", HttpStatus.OK);
    }
    
    @ApiOperation(value = "greeting", notes = "greeting")
    @PutMapping("/api/greeting")
    public ResponseEntity<String> testPut() {
    	return new ResponseEntity<>("AUTH SERVICE PUT METHOD", HttpStatus.OK);
    }
    
}
