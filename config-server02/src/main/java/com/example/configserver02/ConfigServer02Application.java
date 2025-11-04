package com.example.configserver02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;


@EnableConfigServer
@SpringBootApplication
public class ConfigServer02Application {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServer02Application.class, args);
    }

}
