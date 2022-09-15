package com.tsai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/***
 * 配置启动类 SpringBootApplication
 * 配置 Sl4j 打印日志
 */
@Slf4j
@SpringBootApplication
public class DelivaryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DelivaryApplication.class,args);
        log.info("项目启动成功...");
    }
}
