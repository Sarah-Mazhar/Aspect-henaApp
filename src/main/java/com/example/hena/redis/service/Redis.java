package com.example.hena.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.time.Duration;

@Service
public class Redis {

    private final StringRedisTemplate redisTemplate;
    private final ValueOperations<String, String> valueOps;

    @Autowired
    public Redis(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
    }

    public void set(String key, String value) {
        valueOps.set(key, value);
    }

    public void set(String key, String value, Duration timeout) {
        valueOps.set(key, value, timeout);
    }

    @Cacheable(value = "cachedData", key = "#key")
    public String get(String key) {
        System.out.println("Fetching from Redis directly...");
        return valueOps.get(key);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @CacheEvict(value = "cachedData", key = "#key")
    public Boolean setIfAbsent(String key, String value, Duration timeout) {
        return valueOps.setIfAbsent(key, value, timeout);
    }

    public Long increment(String key, Duration ttl) {
        Long count = valueOps.increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, ttl);
        }
        return count;
    }
}
