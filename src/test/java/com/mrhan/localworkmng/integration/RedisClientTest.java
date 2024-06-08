/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.integration;

import com.mrhan.localworkmng.integration.redis.RedisClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @Author yuhang
 * @Date 2022-12-20 14:59
 * @Description
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisClientTest {

    @Resource
    private RedisClient redisClient;

    @Test
    public void setTest() {
        redisClient.zSetSet("TEST","A", 4, 1000 * 60 * 30);
        redisClient.zSetSet("TEST","B", 2, 1000 * 60 * 30);
        redisClient.zSetSet("TEST","C", 1, 1000 * 60 * 30);
        redisClient.zSetSet("TEST","D", 3, 1000 * 60 * 30);
        System.out.println(redisClient.zSetGet("TEST", 21, 32));
    }

}
