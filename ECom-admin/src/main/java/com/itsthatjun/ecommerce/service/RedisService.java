package com.itsthatjun.ecommerce.service;

import io.swagger.annotations.ApiOperation;

public interface RedisService {

    @ApiOperation(value = "")
    void set(String key, String value);

    @ApiOperation(value = "")
    String get(String key);

    @ApiOperation(value = "")
    boolean expire(String key, long expire);

    @ApiOperation(value = "")
    void remove(String key);

    @ApiOperation(value = "")
    Long increment(String key, long delta);
}
