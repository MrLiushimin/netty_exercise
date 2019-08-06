package com.liushimin.hchat;

import com.liushimin.hchat.utils.IdWorker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan(basePackages = "com.liushimin.hchat.mapper")
public class Application {

    @Bean
    public IdWorker idWorker() {
        return new IdWorker(0, 0);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
