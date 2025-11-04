package com.example.apigateway02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ApiGateway02Application {

    public static void main(String[] args) {
        SpringApplication.run(ApiGateway02Application.class, args);
    }

    @Bean
    @LoadBalanced   // ✅ permet à WebClient de résoudre AUTHORIZATION-SERVICE via Eureka
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
