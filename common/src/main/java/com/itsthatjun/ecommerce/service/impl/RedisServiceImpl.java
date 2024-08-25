package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.service.RedisService;

public class RedisServiceImpl implements RedisService {
    @Override
    public void set(String key, String value) {

    }

    @Override
    public String get(String key) {
        return "";
    }

    @Override
    public void delete(String key) {

    }

    @Override
    public boolean hasKey(String key) {
        return false;
    }

    @Override
    public void expire(String key, long timeout) {

    }
}
