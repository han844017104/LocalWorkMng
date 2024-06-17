package com.mrhan.localworkmng.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yuhang
 * @Date 2024-06-13 22:29
 * @Description
 */
@Configuration
@ConfigurationProperties(prefix = "biz.f95")
@Getter
@Setter
public class F95BizConfiguration {

    private String gameSyncLimiter;

}
