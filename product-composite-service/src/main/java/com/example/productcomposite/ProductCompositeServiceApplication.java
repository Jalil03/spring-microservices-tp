package com.example.productcomposite;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.productcomposite.clients")
public class ProductCompositeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCompositeServiceApplication.class, args);
    }

    // ✅ Composant interne pour afficher le port de l’instance au démarrage
    @Component
    static class StartupLogger implements CommandLineRunner {

        @Value("${server.port}")
        private String port;

        @Override
        public void run(String... args) {
            System.out.println("✅ Instance Product-Composite-Service démarrée sur le port : " + port);
        }
    }

    // 🔍 Vérification que la configuration vient du Config Server
    @Component
    static class ConfigCheck {

        @Value("${server.port}")
        private String port;

        @PostConstruct
        public void logConfigSource() {
            System.out.println("🔍 Configuration chargée depuis le Config Server (port configuré = " + port + ")");
        }
    }
}
