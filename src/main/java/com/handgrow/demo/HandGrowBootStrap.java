package com.handgrow.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HandGrowBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(HandGrowBootStrap.class, args);
    }
}
