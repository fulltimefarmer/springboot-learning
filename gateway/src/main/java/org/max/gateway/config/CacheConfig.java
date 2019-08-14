package org.max.gateway.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(keySerializer());
        redisTemplate.setHashKeySerializer(keySerializer());
        redisTemplate.setValueSerializer(valueSerializer());
        redisTemplate.setHashValueSerializer(valueSerializer());
        return redisTemplate;
    }
    
    @Bean(name = "caffeineCache")
    public CacheManager cacheManagerWithCaffeine(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder()
                .initialCapacity(11)
                .maximumSize(19)
                .expireAfterWrite(3, TimeUnit.MINUTES);
        cacheManager.setCaffeine(caffeine);
        cacheManager.setCacheNames(getNames());
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }

    private static List<String> getNames(){
        List<String> names = Lists.newArrayListWithCapacity(1);
        names.add("authServiceCache");
        return names;
    }

    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    private RedisSerializer<Object> valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
    
}
