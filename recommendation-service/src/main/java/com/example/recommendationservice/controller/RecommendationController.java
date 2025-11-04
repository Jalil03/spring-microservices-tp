package com.example.recommendationservice.controller;

import com.example.recommendationservice.model.Recommendation;
import com.example.recommendationservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationRepository repo;

    @Value("${server.port}")
    private String port;

    // ✅ GET : récupérer les recommandations d’un produit
    @GetMapping
    public List<Recommendation> getByProductId(@RequestParam("productId") Long productId) {
        log.info("📦 Instance Recommendation-Service (port={}) traite la requête pour productId={}", port, productId);
        return repo.findByProductId(productId);
    }

    // ✅ POST : créer une recommandation (compatible avec ProductCompositeIntegration)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recommendation create(@RequestParam("productId") Long productId,
                                 @RequestBody Recommendation rec) {
        rec.setProductId(productId); // 🟢 on associe la recommandation au bon produit
        log.info("💡 Création d’une recommandation pour productId={} par {} sur port={}",
                productId, rec.getAuthor(), port);
        return repo.save(rec);
    }

    // ✅ DELETE : supprimer toutes les recommandations d’un produit
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByProductId(@PathVariable Long productId) {
        log.warn("🗑️ Suppression des recommandations pour productId={} sur port={}", productId, port);
        repo.deleteAll(repo.findByProductId(productId));
    }
}
