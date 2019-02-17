package com.hupan.home.smartgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("com.hupan.home.smartgateway")
public class SmartGatewayApplication {
    public static void main(String[] args){
        SpringApplication.run(SmartGatewayApplication.class, args);
    }
}
