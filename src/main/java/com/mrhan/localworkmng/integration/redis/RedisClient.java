/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.integration.redis;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.mrhan.localworkmng.util.JsonUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author yuhang
 * @Date 2022-12-20 13:54
 * @Description
 */
@Component
public class RedisClient {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public long zSetGetCount(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForZSet().size(key)).orElse(0L);
    }

    public List<String> zSetGet(String key, long start, long end) {
        Set<String> range = stringRedisTemplate.opsForZSet().range(key, start, end);
        return Optional.ofNullable(range).map(Lists::newArrayList).orElse(Lists.newArrayList());
    }

    public <T> List<T> zSetGetJsonObj(String key, long start, long end, Class<T> resultClass) {
        return zSetGet(key, start, end)
                .stream().map(e -> JSONObject.parseObject(e, resultClass))
                .collect(Collectors.toList());
    }

    public boolean zSetSet(String key, String value, double score, long expireMs) {
        Boolean writeSuccess = Optional.ofNullable(stringRedisTemplate.opsForZSet().add(key, value, score)).orElse(
                false);
        if (writeSuccess && expireMs >= 0L) {
            return Optional.ofNullable(stringRedisTemplate.expire(key, expireMs, TimeUnit.MILLISECONDS)).orElse(false);
        }
        return false;
    }

    public boolean zSetSet(String key, Object value, double score, long expireMs) {
        return zSetSet(key, value2Str(value), score, expireMs);
    }

    public void zSetBatchSet(String key, List<Pair<String, Double>> values, long expireMs) {
        List<List<Pair<String, Double>>> partition = ListUtil.partition(values, 10000);
        for (List<Pair<String, Double>> pairs : partition) {
            stringRedisTemplate.opsForZSet().add(key, pairs.stream()
                    .map(pair -> ZSetOperations.TypedTuple.of(pair.getLeft(), pair.getRight()))
                    .collect(Collectors.toSet())
            );
        }
        stringRedisTemplate.expire(key, expireMs, TimeUnit.MILLISECONDS);
    }

    public boolean remove(String key) {
        return Optional.ofNullable(stringRedisTemplate.delete(key)).orElse(false);
    }

    private String value2Str(Object value) {
        String strV;
        if (value instanceof String) {
            strV = ((String) value);
        } else {
            strV = JsonUtil.toJsonString(value);
        }
        return strV;
    }

}
