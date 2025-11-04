package com.example.productcomposite.controller;

import com.example.productcomposite.api.ProductAggregate;
import com.example.productcomposite.service.ProductCompositeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/product-composite")
public class ProductCompositeController {

    private static final Logger log = LoggerFactory.getLogger(ProductCompositeController.class);

    private final ProductCompositeService service;

    @Value("${server.port}")
    private String compositePort; // ✅ Port de l’instance composite

    public ProductCompositeController(ProductCompositeService service) {
        this.service = service;
    }

    // ✅ GET : récupérer les données agrégées
    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> getProductComposite(@PathVariable int productId) {
        log.info("🎯 Requête reçue sur ProductCompositeController (port={}) pour productId={}", compositePort, productId);

        // 🔹 Appel du service d’agrégation
        ProductAggregate aggregate = service.getAggregate(productId);

        // 🔹 Préparer la réponse enrichie
        Map<String, Object> response = new HashMap<>();
        response.put("compositeInstancePort", compositePort);
        response.put("product", aggregate.getProduct());
        response.put("reviews", aggregate.getReviews());
        response.put("recommendations", aggregate.getRecommendations());

        // 🔹 Informations simulées sur les instances utilisées (pour visualiser le load balancing)
        Map<String, Object> instancesInfo = new HashMap<>();
        instancesInfo.put("productServiceInstance", "✅ LoadBalancer → Product-Service instance (port aléatoire 8081 / 8031)");
        instancesInfo.put("reviewServiceInstance", "✅ LoadBalancer → Review-Service instance (port aléatoire 8082 / 8032)");
        instancesInfo.put("recommendationServiceInstance", "✅ LoadBalancer → Recommendation-Service instance (port aléatoire 8083 / 8033)");
        instancesInfo.put("compositeInstance", compositePort);

        response.put("instancesUsed", instancesInfo);

        log.info("✅ Réponse envoyée avec les infos des instances pour productId={}", productId);

        return ResponseEntity.ok(response);
    }

    // ✅ POST : créer un nouveau produit
    @PostMapping
    public ResponseEntity<String> createProductComposite(@RequestBody ProductAggregate body) {
        log.info("📦 Requête POST reçue pour création du produit : {}", body.getProduct().getName());

        if (body.getProduct().getWeight() > 100) {
            log.warn("❌ Poids invalide ({}) - doit être <= 100", body.getProduct().getWeight());
            return ResponseEntity.badRequest().body("❌ Le poids du produit ne doit pas dépasser 100 !");
        }

        service.createAggregate(body);
        return ResponseEntity.ok("✅ Produit créé avec succès !");
    }

    // ✅ PUT : mettre à jour un produit existant
    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProductComposite(@PathVariable int productId,
                                                         @RequestBody ProductAggregate body) {
        log.info("✏️ Requête PUT reçue pour mise à jour du produit id={} avec poids={}",
                productId, body.getProduct().getWeight());

        if (body.getProduct().getWeight() > 100) {
            log.warn("❌ Poids invalide ({}) - doit être <= 100", body.getProduct().getWeight());
            return ResponseEntity.badRequest().body("❌ Le poids du produit ne doit pas dépasser 100 !");
        }

        service.updateAggregate(productId, body);
        return ResponseEntity.ok("✅ Produit mis à jour avec succès !");
    }
}
