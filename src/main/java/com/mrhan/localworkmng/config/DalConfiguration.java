/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author yuhang
 * @Date 2022-08-22 10:59
 * @Description
 */
@Component
@EnableTransactionManagement
@MapperScan(basePackages = {"com.mrhan.localworkmng.dal.trans.mapper"})
public class DalConfiguration {
}
