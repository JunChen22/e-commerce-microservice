package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    // shared operations

    @Override
    public Mono<Boolean> expire(String key, long time) {
        return redisTemplate.expire(key, Duration.ofSeconds(time));
    }

    @Override
    public Mono<Duration> getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    @Override
    public Mono<Boolean> hasKey(String key) {
        return redisTemplate.hasKey(key);
    }


    // String related operations

    @Override
    public Mono<Boolean> set(String key, Object value, long time) {
        return redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(time));
    }

    @Override
    public Mono<Boolean> set(String key, Object value) {
        return redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Mono<Object> get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Mono<Long> del(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Mono<Long> del(List<String> keys) {
        // Convert List to Flux
        Flux<String> keyFlux = Flux.fromIterable(keys);
        return redisTemplate.delete(keyFlux);
    }

    @Override
    public Mono<Long> incr(String key, int delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Mono<Long> decr(String key, int delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // List related operations

    @Override
    public Mono<List<Object>> lRange(String key, int start, int end) {
        return redisTemplate.opsForList().range(key, start, end).collectList();
    }

    @Override
    public Mono<Long> lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public Mono<Object> lIndex(String key, int index) {
        return redisTemplate.opsForList().index(key, index);
    }

    @Override
    public Mono<Long> lPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public Mono<Long> lPush(String key, Object value, long time) {
        return redisTemplate.opsForList().rightPush(key, value)
                .flatMap(index -> expire(key, time).thenReturn(index));
    }

    @Override
    public Mono<Long> lPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    public Mono<Long> lPushAll(String key, Long time, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values)
                .flatMap(count -> expire(key, time).thenReturn(count));
    }

    @Override
    public Mono<Long> lRemove(String key, int count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }


    // Set operations related

    @Override
    public Mono<Set<Object>> sMembers(String key) {
        return redisTemplate.opsForSet()
                .members(key)
                .collect(Collectors.toSet());
    }

    @Override
    public Mono<Long> sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public Mono<Long> sAdd(String key, long time, Object... values) {
        return redisTemplate.opsForSet().add(key, values)
                .flatMap(count -> expire(key, time).thenReturn(count));
    }

    @Override
    public Mono<Boolean> sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Mono<Long> sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Mono<Long> sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }


    // Hash or map related operations

    @Override
    public Mono<Object> hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public Mono<Boolean> hSet(String key, String hashKey, Object value, long time) {
        return redisTemplate.opsForHash().put(key, hashKey, value)
                .flatMap(result -> expire(key, time).thenReturn(result));
    }

    @Override
    public Mono<Void> hSet(String key, String hashKey, Object value) {
        return redisTemplate.opsForHash().put(key, hashKey, value).then();
    }

    @Override
    public Mono<Map<Object, Object>> hGetAll(String key) {
        return redisTemplate.opsForHash()
                .entries(key)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    @Override
    public Mono<Boolean> hSetAll(String key, Map<String, Object> map, long time) {
        return redisTemplate.opsForHash().putAll(key, map)
                .flatMap(result -> expire(key, time).thenReturn(result));
    }

    @Override
    public Mono<Void> hSetAll(String key, Map<String, ?> map) {
        return redisTemplate.opsForHash().putAll(key, map).then();
    }

    @Override
    public Mono<Boolean> hDel(String key) {
        return redisTemplate.opsForHash().delete(key);
    }

    @Override
    public Mono<Boolean> hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public Mono<Long> hIncr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    @Override
    public Mono<Long> hDecr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }
}
