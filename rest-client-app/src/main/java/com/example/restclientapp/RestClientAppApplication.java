package com.example.restclientapp;

import com.example.restclientapp.service.ProductApiClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class RestClientAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestClientAppApplication.class, args);
    }

    @Bean
    CommandLineRunner run(ProductApiClient client) {
        return args -> {

            // 1️⃣ Tester GET
            client.getProductById(1);

            // 2️⃣ Tester POST
            Map<String, Object> newProduct = Map.of(
                    "product", Map.of("productId", 99, "name", "Capteur d’humidité", "weight", 35),
                    "reviews", List.of(),
                    "recommendations", List.of()
            );
            client.createProduct(newProduct);

            // 3️⃣ Tester PUT
            Map<String, Object> updatedProduct = Map.of(
                    "product", Map.of("productId", 99, "name", "Capteur d’humidité V2", "weight", 45),
                    "reviews", List.of(),
                    "recommendations", List.of()
            );
            client.updateProduct(99, updatedProduct);

            // 4️⃣ Vérifier après modification
            client.getProductById(99);
        };
    }
}
