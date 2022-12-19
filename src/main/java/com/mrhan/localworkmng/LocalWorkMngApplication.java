package com.mrhan.localworkmng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.mrhan.localworkmng"})
@EnableScheduling
public class LocalWorkMngApplication {

    public static void main(String[] args) {
        beforeRun();
        SpringApplication.run(LocalWorkMngApplication.class, args);
    }

    private static void beforeRun() {

    }

}
