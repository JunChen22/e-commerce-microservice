package com.itsthatjun.ecommerce.service;

public interface RedisService {
    void set(String key, String value);
    String get(String key);
    void delete(String key);
    boolean hasKey(String key);
    void expire(String key, long timeout);
}
