package com.itsthatjun.ecommerce.service;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {

    // shared operations

    /**
     * set expiration
     */
    Mono<Boolean> expire(String key, long time);

    /**
     * get expiration
     */
    Mono<Duration> getExpire(String key);

    /**
     * check if key exists
     */
    Mono<Boolean> hasKey(String key);


    // String related operations

    /**
     * set a value with an optional expiration time
     */
    Mono<Boolean> set(String key, Object value, long time);

    /**
     * set a value
     */
    Mono<Boolean> set(String key, Object value);

    /**
     * get the value for a given key
     */
    Mono<Object> get(String key);

    /**
     * delete a key-value pair
     */
    Mono<Long> del(String key);

    /**
     * delete multiple key-value pairs
     */
    Mono<Long> del(List<String> keys);

    /**
     * increment the value of a key by a specified delta
     */
    Mono<Long> incr(String key, int delta);

    /**
     * decrement the value of a key by a specified delta
     */
    Mono<Long> decr(String key, int delta);


    // List related operations

    /**
     * get a range of elements from a list
     */
    Mono<List<Object>> lRange(String key, int start, int end);

    /**
     * get the size of a list
     */
    Mono<Long> lSize(String key);

    /**
     * get the element at a specific index in a list
     */
    Mono<Object> lIndex(String key, int index);

    /**
     * push an element to the right of a list
     */
    Mono<Long> lPush(String key, Object value);

    /**
     * push an element to the right of a list with an expiration time
     */
    Mono<Long> lPush(String key, Object value, long time);

    /**
     * push multiple elements to the right of a list
     */
    Mono<Long> lPushAll(String key, Object... values);

    /**
     * push multiple elements to the right of a list with an expiration time
     */
    Mono<Long> lPushAll(String key, Long time, Object... values);

    /**
     * remove elements from a list by count
     */
    Mono<Long> lRemove(String key, int count, Object value);


    // Set operations related

    /**
     * get all members of a set
     */
    Mono<Set<Object>> sMembers(String key);

    /**
     * add one or more members to a set
     */
    Mono<Long> sAdd(String key, Object... values);

    /**
     * add members to a set with an expiration time
     */
    Mono<Long> sAdd(String key, long time, Object... values);

    /**
     * check if a value is a member of a set
     */
    Mono<Boolean> sIsMember(String key, Object value);

    /**
     * get the size of a set
     */
    Mono<Long> sSize(String key);

    /**
     * remove one or more members from a set
     */
    Mono<Long> sRemove(String key, Object... values);


    // Hash or map related operations

    /**
     * get the value associated with a hash key
     */
    Mono<Object> hGet(String key, String hashKey);

    /**
     * set a hash key-value pair with an optional expiration time
     */
    Mono<Boolean> hSet(String key, String hashKey, Object value, long time);

    /**
     * set a hash key-value pair
     */
    Mono<Void> hSet(String key, String hashKey, Object value);

    /**
     * List all key-value pairs in a hash
     */
    Mono<Map<Object, Object>> hListAll(String key);

    /**
     * Set multiple key-value pairs in a hash with an optional expiration time
     */
    Mono<Boolean> hSetAll(String key, Map<String, Object> map, long time);

    /**
     * Set multiple key-value pairs in a hash
     */
    Mono<Void> hSetAll(String key, Map<String, ?> map);

    /**
     * Delete one hash key
     */
    Mono<Boolean> hDel(String key);

    /**
     * Check if a hash key exists
     */
    Mono<Boolean> hHasKey(String key, String hashKey);

    /**
     * Increment the value associated with a hash key
     */
    Mono<Long> hIncr(String key, String hashKey, Long delta);

    /**
     * Decrement the value associated with a hash key
     */
    Mono<Long> hDecr(String key, String hashKey, Long delta);
}