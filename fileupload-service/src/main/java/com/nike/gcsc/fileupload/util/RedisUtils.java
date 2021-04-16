 package com.nike.gcsc.fileupload.util;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.util.CollectionUtils;

import lombok.NonNull;

/**
 * @author roger yang
 * @date 10/31/2019
 */
public class RedisUtils {
    private static StringRedisTemplate redisTemplate;
    private static HashOperations<String, String, String> hashOps;
    private static ZSetOperations<String, String> zsetOps;
    private static SetOperations<String, String> setOps;
    
    private RedisUtils() {}
    
    public static RedisTemplate<String, String> getStringRedisTemplate() {
        return redisTemplate;
    }
    
    public static void setRedisTemplate(@NonNull StringRedisTemplate rt) {
        redisTemplate = rt;
        hashOps = redisTemplate.opsForHash();
        zsetOps = redisTemplate.opsForZSet();
        setOps = redisTemplate.opsForSet();
    }
    
    public static boolean hasKey(@NonNull String key) {
        return redisTemplate.hasKey(key);
    }
    
    public static void expire(@NonNull String key, long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }
    
    public static void expire(@NonNull String key, long timeout, @NonNull TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }
    
    /**
     *  --------------begin hash ops --------------------
     */
    public static boolean hasHashKey(@NonNull String key, @NonNull String hashKey) {
        return hashOps.hasKey(key, hashKey);
    }
    
    public static void hashPut(@NonNull String key, @NonNull String hashKey, @NonNull String value) {
        hashOps.put(key, hashKey, value);
    }
    
    public static void hashPutIfAbsent(@NonNull String key, @NonNull String hashKey, @NonNull String value) {
        hashOps.putIfAbsent(key, hashKey, value);
    }
    
    public static void hashPutAll(@NonNull String key, Map<String, String> map) {
        hashOps.putAll(key, map);
    }
    
    public static String hashGet(@NonNull String key, @NonNull String hashKey) {
        return hashOps.get(key, hashKey);
    }
    
    public static Map<String, String> hashGetAll(@NonNull String key) {
        return hashOps.entries(key);
    }
    
    public static Long hashIncrement(@NonNull String key, @NonNull String hashKey) {
        return hashOps.increment(key, hashKey, 1);
    }
    
    public static Long hashIncrement(@NonNull String key, @NonNull String hashKey, Long value) {
        return hashOps.increment(key, hashKey, value);
    }
    
    /**
     * --------------begin zset ops --------------------
     */
    public static Long zAddAll(@NonNull String key, Set<Integer> values) {
        Set<TypedTuple<String>> tuples = values.stream().map(item -> {
            TypedTuple<String> val = new DefaultTypedTuple<>(String.valueOf(item), Double.valueOf(String.valueOf(item)));
            return val;
        }).collect(Collectors.toSet());
        return zsetOps.add(key, tuples);
    }
    
    public static String zGetFirst(@NonNull String key) {
        Set<String> set = zsetOps.range(key, 0L, 0L);
        if(CollectionUtils.isEmpty(set)) {
            return null;
        }
        return set.iterator().next();
    }
    
    public static String zGetNextValue(@NonNull String key, double currentScore) {
        double min = currentScore + 1;
        double max = currentScore+100;
        Set<String> set = zsetOps.rangeByScore(key, min, max);
        if(CollectionUtils.isEmpty(set)) {
            return null;
        }
        return set.iterator().next();
    }
    
    public static Double zScore(@NonNull String key, @NonNull String value) {
        return zsetOps.score(key, value);
    }
    
    public static Long zSize(@NonNull String key) {
        return zsetOps.size(key);
    }
    
    public static Long zRemove(@NonNull String key, @NonNull String value) {
        return zsetOps.remove(key, value);
    }
    
    public static Set<String> zGetAll(@NonNull String key) {
        return zsetOps.range(key, 0, -1);
    }
    
    /**
     * --------------- begin set ops ----------------------
     */
    public static Long sAdd(@NonNull String key, @NonNull String value) {
        return setOps.add(key, value);
    }
}
