package org.max.learning.common.controller;


import java.util.concurrent.TimeUnit;

import org.max.learning.common.dto.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/cache")
public class CacheController {
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@ApiOperation(value = "get from cache", notes = "get from cache")
    @GetMapping("/get/{key}")
    public ResponseEntity<String> get(
    		@PathVariable(name = "key") String key) {
		return new ResponseEntity<String>(redisTemplate.opsForValue().get(key), HttpStatus.OK);
    }
	
    @ApiOperation(value = "post into cache", notes = "post into cache")
    @GetMapping("/post/{key}/{value}")
    public ResponseEntity<String> post(
    		@PathVariable(name = "key") String key,
    		@PathVariable(name = "value") String value) {
    	redisTemplate.opsForValue().set(key, value, 10, TimeUnit.MINUTES);
        return new ResponseEntity<String>(value, HttpStatus.OK);
    }
    
}

