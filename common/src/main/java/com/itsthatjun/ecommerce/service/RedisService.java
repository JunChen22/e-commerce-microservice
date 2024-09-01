package com.itsthatjun.ecommerce.service;

import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {

    // shared operations

    @ApiOperation(value = "set expiration")
    Mono<Boolean> expire(String key, long time);

    @ApiOperation(value = "get expiration")
    Mono<Duration> getExpire(String key);

    @ApiOperation(value = "check if key exists")
    Mono<Boolean> hasKey(String key);


    // String related operations

    @ApiOperation(value = "Set a value with an optional expiration time")
    Mono<Boolean> set(String key, Object value, long time);

    @ApiOperation(value = "Set a value")
    Mono<Boolean> set(String key, Object value);

    @ApiOperation(value = "Get the value for a given key")
    Mono<Object> get(String key);

    @ApiOperation(value = "Delete a key-value pair")
    Mono<Long> del(String key);

    @ApiOperation(value = "Delete multiple key-value pairs")
    Mono<Long> del(List<String> keys);

    @ApiOperation(value = "Increment the value of a key by a specified delta")
    Mono<Long> incr(String key, int delta);

    @ApiOperation(value = "Decrement the value of a key by a specified delta")
    Mono<Long> decr(String key, int delta);


    // List related operations

    @ApiOperation(value = "Get a range of elements from a list")
    Mono<List<Object>> lRange(String key, int start, int end);

    @ApiOperation(value = "Get the size of a list")
    Mono<Long> lSize(String key);

    @ApiOperation(value = "Get the element at a specific index in a list")
    Mono<Object> lIndex(String key, int index);

    @ApiOperation(value = "Push an element to the right of a list")
    Mono<Long> lPush(String key, Object value);

    @ApiOperation(value = "Push an element to the right of a list with an expiration time")
    Mono<Long> lPush(String key, Object value, long time);

    @ApiOperation(value = "Push multiple elements to the right of a list")
    Mono<Long> lPushAll(String key, Object... values);

    @ApiOperation(value = "Push multiple elements to the right of a list with an expiration time")
    Mono<Long> lPushAll(String key, Long time, Object... values);

    @ApiOperation(value = "Remove elements from a list by count")
    Mono<Long> lRemove(String key, int count, Object value);


    // Set operations related

    @ApiOperation(value = "Get all members of a set")
    Mono<Set<Object>> sMembers(String key);

    @ApiOperation(value = "Add one or more members to a set")
    Mono<Long> sAdd(String key, Object... values);

    @ApiOperation(value = "Add members to a set with an expiration time")
    Mono<Long> sAdd(String key, long time, Object... values);

    @ApiOperation(value = "Check if a value is a member of a set")
    Mono<Boolean> sIsMember(String key, Object value);

    @ApiOperation(value = "Get the size of a set")
    Mono<Long> sSize(String key);

    @ApiOperation(value = "Remove one or more members from a set")
    Mono<Long> sRemove(String key, Object... values);


    // Hash or map related operations

    @ApiOperation(value = "Get the value associated with a hash key")
    Mono<Object> hGet(String key, String hashKey);

    @ApiOperation(value = "Set a hash key-value pair with an optional expiration time")
    Mono<Boolean> hSet(String key, String hashKey, Object value, long time);

    @ApiOperation(value = "Set a hash key-value pair")
    Mono<Void> hSet(String key, String hashKey, Object value);

    @ApiOperation(value = "Get all key-value pairs in a hash")
    Mono<Map<Object, Object>> hGetAll(String key);

    @ApiOperation(value = "Set multiple key-value pairs in a hash with an optional expiration time")
    Mono<Boolean> hSetAll(String key, Map<String, Object> map, long time);

    @ApiOperation(value = "Set multiple key-value pairs in a hash")
    Mono<Void> hSetAll(String key, Map<String, ?> map);

    @ApiOperation(value = "Delete one hash key")
    Mono<Boolean> hDel(String key);

    @ApiOperation(value = "Check if a hash key exists")
    Mono<Boolean> hHasKey(String key, String hashKey);

    @ApiOperation(value = "Increment the value associated with a hash key")
    Mono<Long> hIncr(String key, String hashKey, Long delta);

    @ApiOperation(value = "Decrement the value associated with a hash key")
    Mono<Long> hDecr(String key, String hashKey, Long delta);
}