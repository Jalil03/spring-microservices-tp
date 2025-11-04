package com.example.apigateway02.filter;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.retry.Retry;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@Component
public class AuthorizationFilter implements GlobalFilter, Ordered {

    private final WebClient.Builder webClientBuilder;

    /**
     * 🔹 Utilise le service Eureka s'il est disponible.
     * 🔹 Sinon, bascule automatiquement sur l’URL locale.
     */
    @Value("${auth.service.url:lb://AUTHORIZATION-SERVICE/auth/validate}")
    private String authServiceUrl;

    public AuthorizationFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // ✅ Exclure la route d'authentification pour éviter les boucles
        if (path.startsWith("/auth")) {
            return chain.filter(exchange);
        }

        HttpHeaders headers = exchange.getRequest().getHeaders();
        if (!headers.containsKey("username") || !headers.containsKey("password") || !headers.containsKey("role")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String username = headers.getFirst("username");
        String password = headers.getFirst("password");
        String role = headers.getFirst("role");

        // ✅ Appel non bloquant vers le AUTHORIZATION-SERVICE
        return webClientBuilder.build()
                .get()
                .uri(authServiceUrl)
                .header("username", username)
                .header("password", password)
                .header("role", role)
                // 🔁 Réessaye 3 fois (utile quand Eureka n’a pas encore enregistré le service)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .flatMap(response -> {
                    if (response != null && response.startsWith("AUTHORIZED")) {
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                })
                .onErrorResume(e -> {
                    // 🔄 Fallback automatique en cas d’échec de découverte Eureka
                    System.err.println("⚠️ Authorization service unreachable: " + e.getMessage());
                    String fallbackUrl = "http://localhost:8060/auth/validate";
                    System.out.println("➡️ Tentative fallback vers " + fallbackUrl);

                    return webClientBuilder.build()
                            .get()
                            .uri(fallbackUrl)
                            .header("username", username)
                            .header("password", password)
                            .header("role", role)
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMap(response -> {
                                if (response != null && response.startsWith("AUTHORIZED")) {
                                    return chain.filter(exchange);
                                } else {
                                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                    return exchange.getResponse().setComplete();
                                }
                            })
                            .onErrorResume(err -> {
                                System.err.println("❌ Fallback aussi indisponible: " + err.getMessage());
                                exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                                return exchange.getResponse().setComplete();
                            });
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }



    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthorizationFilter.class);


    @Bean
    public GlobalFilter customFilter() {
        return (exchange, chain) -> {
            log.info("➡️ Requête reçue : {} {}",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getURI());
            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                    log.info("⬅️ Réponse envoyée : {}", exchange.getResponse().getStatusCode())
            ));
        };
    }



}
