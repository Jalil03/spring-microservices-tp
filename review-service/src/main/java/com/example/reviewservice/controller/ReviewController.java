package com.example.reviewservice.controller;

import com.example.reviewservice.model.Review;
import com.example.reviewservice.repository.ReviewRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository repo;

    // 🟢 Injecte le port pour identifier l’instance
    @Value("${server.port}")
    private String port;

    public ReviewController(ReviewRepository repo) {
        this.repo = repo;
    }

    // ============================================================
    // ✅ GET : récupérer toutes les reviews d’un produit
    // ============================================================
    @GetMapping("/product/{productId}")
    public List<Review> byProduct(@PathVariable int productId) {
        log.info("📦 Instance Review-Service (port={}) traite la requête pour productId={}", port, productId);
        return repo.findByProductId(productId);
    }

    // ============================================================
    // ✅ POST : création directe (manuel, via Postman ou tests)
    // ============================================================
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@Valid @RequestBody Review r) {
        log.info("🟢 Création manuelle d’une review pour productId={} sur le port={}", r.getProductId(), port);
        return repo.save(r);
    }

    // ============================================================
    // ✅ POST : compatible avec ProductCompositeIntegration
    // ============================================================
    @PostMapping("/product/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Review createByProduct(@PathVariable int productId, @Valid @RequestBody Review r) {
        r.setProductId(productId);
        log.info("📝 Création d’une review via product-composite pour productId={} par {} sur port={}",
                productId, r.getAuthor(), port);
        return repo.save(r);
    }

    // ============================================================
    // ✅ DELETE : supprimer toutes les reviews d’un produit
    // ============================================================
    @DeleteMapping("/product/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByProduct(@PathVariable int productId) {
        log.warn("🗑️ Suppression des reviews pour productId={} sur port={}", productId, port);
        repo.deleteAll(repo.findByProductId(productId));
    }
}
