package org.max.learning.common.controller;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.max.learning.common.dto.Entity;
import org.max.learning.common.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/cache")
public class CacheController {
	
	private Cache<String, Entity> cache = Caffeine.newBuilder()
			.initialCapacity(99)
			.maximumSize(999)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@ApiOperation(value = "get from cache", notes = "get from cache")
    @GetMapping("/get/{key}")
    public ResponseEntity<Entity> get(@PathVariable(name = "key") String key) {
//		Entity e = (Entity)redisTemplate.opsForValue().get(key);
		Entity entity = cache.get(key, k -> getFromRedis(k));
		return new ResponseEntity<Entity>(entity, HttpStatus.OK);
    }
	
    @ApiOperation(value = "post into cache", notes = "post into cache")
    @GetMapping("/post/{key}/{value}")
    public ResponseEntity<String> post(
    		@PathVariable(name = "key") String key,
    		@PathVariable(name = "value") String value) {
    	Entity e = new Entity();
    	e.setKey(key);
    	e.setValue(value);
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
    	redisTemplate.opsForValue().set(e.getKey(), e, 5, TimeUnit.MINUTES);
        return new ResponseEntity<String>(e.getKey(), HttpStatus.OK);
    }
    
    private Entity getFromRedis(String key) {
    	return (Entity)redisTemplate.opsForValue().get(key);
    }
    
}

