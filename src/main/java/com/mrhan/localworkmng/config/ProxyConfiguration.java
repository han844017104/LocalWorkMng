package com.mrhan.localworkmng.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yuhang
 * @Date 2024-06-13 22:27
 * @Description
 */
@Configuration
@ConfigurationProperties(prefix = "spring.net.proxy")
@Getter
@Setter
public class ProxyConfiguration {

    private String host;

    private String port;

}
