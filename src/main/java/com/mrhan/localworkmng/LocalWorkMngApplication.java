package com.mrhan.localworkmng;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.setting.yaml.YamlUtil;
import org.checkerframework.checker.units.qual.C;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication(scanBasePackages = {"com.mrhan.localworkmng"})
@EnableScheduling
public class LocalWorkMngApplication {

    public static void main(String[] args) throws Exception {
        beforeRun();
        SpringApplication.run(LocalWorkMngApplication.class, args);
    }

    private static void beforeRun() throws Exception {
        checkAndRunRedis();
    }

    private static void checkAndRunRedis() throws Exception {
        ClassPathResource resource = new ClassPathResource("application.yaml");
        Dict load = YamlUtil.load(new InputStreamReader(resource.getInputStream()));
        Integer port = load.getByPath("spring.redis.port", Integer.class);
        if (NetUtil.isUsableLocalPort(port)) {
            throw new ExceptionInInitializerError("redis server is not available on port " + port);
        }
    }

}
