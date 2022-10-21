/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.model.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Author yuhang
 * @Date 2022-07-21 11:23
 * @Description
 */
@Getter
public enum TranslateEngineEnum {

    /**
     * 矫正定制
     */
    CUSTOM("CUSTOM", 999),

    /**
     * 测试用引擎，不记录log和入库
     */
    TEST("TestEngine", -1),

    /**
     * 百度翻译
     * <p>
     * 1.10 QPS
     * 2.每月100W字符
     */
    BAIDU("BaiduEngine", 9),

    /**
     * al翻译
     * <p>
     * 1.10 QPS
     * 2.每日10W次调用
     * 3.效果很差,但便宜
     */
    AL("AlEngine", 5),

    /**
     * 腾讯云翻译
     * <p>
     * 1.5 QPS
     * 2.每月免费500W字符
     */
    TENCENT("TencentEngine", 6),

    /**
     * 微软翻译
     * <p>
     * 1.QPS无限制
     * 2.每月免费200W字符
     * 3.真的很快,所以要格外注意字符消耗量
     */
    AZURE("AzureEngine", 7),

    /**
     *
     */
    GOOGLE_GTX("GoogleGTXEngine", 8),
    ;

    private final String engineName;

    private final int priority;

    TranslateEngineEnum(String engineName, int priority) {
        this.engineName = engineName;
        this.priority = priority;
    }

    public static TranslateEngineEnum getByName(String name) {
        return Arrays.stream(TranslateEngineEnum.values()).filter(
                one -> one.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
