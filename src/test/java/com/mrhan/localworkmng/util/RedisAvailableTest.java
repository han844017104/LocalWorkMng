/*
 * F5Loser
 * Copyright (c) 2021-2023 All Rights Reserved
 */
package com.mrhan.localworkmng.util;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import org.junit.Test;

import java.net.SocketException;

/**
 * @Author yuhang
 * @Date 2023-01-18 17:03
 * @Description
 */
public class RedisAvailableTest {

    @Test
    public void availableTest() {
        try {
            HttpUtil.get("http://localhost:6379");
        } catch (HttpException | IORuntimeException e) {
            if (e.getCause() instanceof SocketException) {
                if ("Unexpected end of file from server".equals(e.getCause().getMessage())) {

                }
            }
        }

    }

}
