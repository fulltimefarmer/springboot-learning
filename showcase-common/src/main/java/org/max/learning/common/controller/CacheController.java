package org.max.learning.common.controller;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.max.learning.common.dto.Entity;
import org.max.learning.common.dto.User;
import org.max.learning.common.service.RedisTemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/cache")
public class CacheController {
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
//	private RedisTemplateServiceImpl redisTemplateService;
	
	@ApiOperation(value = "get from cache", notes = "get from cache")
    @GetMapping("/get/{key}")
    public ResponseEntity<Entity> get(@PathVariable(name = "key") String key) {
//		Entity e = redisTemplateService.get(key, Entity.class);
		Entity e = (Entity)redisTemplate.opsForValue().get(key);
		return new ResponseEntity<Entity>(e, HttpStatus.OK);
    }
	
    @ApiOperation(value = "post into cache", notes = "post into cache")
    @GetMapping("/post")
    public ResponseEntity<String> post() {
    	Entity e = new Entity();
    	e.setKey("key:" + Instant.now().getEpochSecond());
    	e.setValue("value");
    	List<User> users = new ArrayList<User>();
    	for(long i=0;i<6;i++) {
    		User u = new User();
    		u.setId(i);
    		u.setUsername("user"+i);
    		u.setType("typeR");
    		u.setCreateDate(new Date(Instant.now().getEpochSecond()));
    		users.add(u);
    	}
    	e.setUserList(users);
    	
//    	redisTemplateService.set(e.getKey(), e);
    	redisTemplate.opsForValue().set(e.getKey(), e, 3, TimeUnit.MINUTES);
        return new ResponseEntity<String>(e.getKey(), HttpStatus.OK);
    }
    
}

