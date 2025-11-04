package com.example.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class ProductServiceApplication {
    public static void main(String[] args) {
        System.setProperty("eureka.instance.hostname", "localhost");
        System.setProperty("eureka.instance.preferIpAddress", "true");
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
