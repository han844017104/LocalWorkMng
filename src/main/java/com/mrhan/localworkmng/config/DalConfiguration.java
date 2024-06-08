/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @Author yuhang
 * @Date 2022-08-22 10:59
 * @Description
 */
@Component
@EnableTransactionManagement
@MapperScan(basePackages = {"com.mrhan.localworkmng.dal.trans.mapper"},
        sqlSessionTemplateRef = "transSqlSessionTemplate")
@MapperScan(basePackages = {"com.mrhan.localworkmng.dal.f95.mapper"},
        sqlSessionTemplateRef = "f95SqlSessionTemplate")
public class DalConfiguration {

    /**
     * --------------- trans dal config start ---------------
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.trans")
    public DataSource transDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public SqlSessionFactory transSqlSessionFactory(@Qualifier("transDataSource") DataSource dataSource) throws
            Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/trans/*.xml"));
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        bean.setPlugins(interceptor);
        return bean.getObject();
    }

    @Bean
    public DataSourceTransactionManager transTransactionManager(@Qualifier("transDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public TransactionTemplate transTransactionTemplate(@Qualifier("transTransactionManager")
                                                        PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public SqlSessionTemplate transSqlSessionTemplate(
            @Qualifier("transSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /** --------------- trans dal config end --------------- */


    /**
     * --------------- f95 dal config start ---------------
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.f95")
    public DataSource f95DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public SqlSessionFactory f95SqlSessionFactory(@Qualifier("f95DataSource") DataSource dataSource) throws
            Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/f95/*.xml"));
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        bean.setPlugins(interceptor);
        return bean.getObject();
    }

    @Bean
    public DataSourceTransactionManager f95TransactionManager(@Qualifier("f95DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public TransactionTemplate f95TransactionTemplate(@Qualifier("f95TransactionManager")
                                                        PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public SqlSessionTemplate f95SqlSessionTemplate(
            @Qualifier("f95SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /** --------------- f95 dal config end --------------- */

}
