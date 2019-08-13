package com.nike.gcsc.gateway.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

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

}
