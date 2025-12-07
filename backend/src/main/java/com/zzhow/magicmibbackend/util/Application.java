package com.zzhow.magicmibbackend.util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * SpringBoot 启动类
 *
 * @author ZZHow
 * create 2025/11/27
 * update 2025/12/7
 */
@SpringBootApplication(scanBasePackages = "com.zzhow.magicmibbackend")
public class Application {
    public static ConfigurableApplicationContext startService(String args) {
        return SpringApplication.run(Application.class, args);
    }
}
