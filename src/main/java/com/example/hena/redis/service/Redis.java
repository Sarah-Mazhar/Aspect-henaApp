package com.example.hena.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.time.Duration;

/**
 * Redis service wrapper for simplified key-value operations using StringRedisTemplate.
 */
@Service
public class Redis {

    private final StringRedisTemplate redisTemplate;
    private final ValueOperations<String, String> valueOps;

    /**
     * Constructor-based injection for Redis template and value operations.
     */
    @Autowired
    public Redis(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
    }

    /**
     * Stores a key-value pair in Redis with no expiration.
     */
    public void set(String key, String value) {
        valueOps.set(key, value);
    }

    /**
     * Stores a key-value pair in Redis with a specified expiration timeout.
     */
    public void set(String key, String value, Duration timeout) {
        valueOps.set(key, value, timeout);
    }

    /**
     * Retrieves the value of a given key from Redis.
     * Uses Spring Cache abstraction to reduce direct Redis hits.
     */
    @Cacheable(value = "cachedData", key = "#key")
    public String get(String key) {
        System.out.println("Fetching from Redis directly...");
        return valueOps.get(key);
    }

    /**
     * Deletes a key from Redis.
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * Stores a value only if the key does not already exist.
     * Also sets a timeout and evicts it from Spring Cache if present.
     */
    @CacheEvict(value = "cachedData", key = "#key")
    public Boolean setIfAbsent(String key, String value, Duration timeout) {
        return valueOps.setIfAbsent(key, value, timeout);
    }

    /**
     * Increments the value of a key.
     * If the key is new (starts at 1), a TTL is applied to it.
     */
    public Long increment(String key, Duration ttl) {
        Long count = valueOps.increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, ttl);
        }
        return count;
    }
}
