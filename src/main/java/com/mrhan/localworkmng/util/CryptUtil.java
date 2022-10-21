/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @Author yuhang
 * @Date 2022-07-18 18:04
 * @Description
 */
public class CryptUtil {

    public static String digestMd5(String text) {
        return DigestUtils.md5Hex(text.getBytes(StandardCharsets.UTF_8));
    }

}
