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
	
    private Entity getFromRedis(String key) {
    	return (Entity)redisTemplate.opsForValue().get(key);
    }
    
}

