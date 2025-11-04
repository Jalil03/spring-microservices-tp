package com.example.restclientapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProductApiClient {

    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String baseUrl;

    @Value("${api.auth.username}")
    private String username;

    @Value("${api.auth.password}")
    private String password;

    @Value("${api.auth.role}")
    private String role;

    public ProductApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", username);
        headers.set("password", password);
        headers.set("role", role);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // ✅ GET
    public void getProductById(int id) {
        String url = baseUrl + "/" + id;
        log.info("➡️ Envoi d'une requête GET vers {}", url);

        HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        log.info("✅ Réponse GET : {}", response.getBody());
    }

    // ✅ POST
    public void createProduct(Map<String, Object> request) {
        log.info("➡️ Envoi d'une requête POST vers {}", baseUrl);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createAuthHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, entity, String.class);

        log.info("✅ Réponse POST : {}", response.getBody());
    }

    // ✅ PUT
    public void updateProduct(int id, Map<String, Object> request) {
        String url = baseUrl + "/" + id;
        log.info("➡️ Envoi d'une requête PUT vers {}", url);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createAuthHeaders());
        restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        log.info("✅ Produit mis à jour avec succès !");
    }
}
