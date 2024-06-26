package com.mrhan.localworkmng;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.net.NetUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.setting.yaml.YamlUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.InputStreamReader;
import java.net.SocketException;

@SpringBootApplication(scanBasePackages = {"com.mrhan.localworkmng"})
@EnableScheduling
public class LocalWorkMngApplication {

    public static void main(String[] args) throws Exception {
        beforeRun();
        SpringApplication.run(LocalWorkMngApplication.class, args);
    }

    private static void beforeRun() throws Exception {
        checkRedis();
    }

    private static void checkRedis() throws Exception {
        ClassPathResource resource = new ClassPathResource("application.yaml");
        Dict load = YamlUtil.load(new InputStreamReader(resource.getInputStream()));
        Integer port = load.getByPath("spring.redis.port", Integer.class);
        if (NetUtil.isUsableLocalPort(port) && !isRedisAvailable(port)) {
            throw new ExceptionInInitializerError("redis server is not available on port " + port);
        }
    }

    private static boolean isRedisAvailable(int port) {
        try {
            HttpUtil.get("http://localhost:" + port);
        } catch (HttpException | IORuntimeException e) {
            if (e.getCause() instanceof SocketException) {
                if ("Unexpected end of file from server".equals(e.getCause().getMessage())) {
                    return true;
                }
            }
        }
        return false;
    }

}
