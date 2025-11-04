package com.example.productcomposite.integration;

import com.example.productcomposite.api.ProductAggregate;
import com.example.productcomposite.clients.ProductClient;
import com.example.productcomposite.clients.RecommendationClient;
import com.example.productcomposite.clients.ReviewClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductCompositeIntegration {

    private static final Logger log = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final ProductClient productClient;
    private final RecommendationClient recommendationClient;
    private final ReviewClient reviewClient;

    public ProductCompositeIntegration(ProductClient productClient,
                                       RecommendationClient recommendationClient,
                                       ReviewClient reviewClient) {
        this.productClient = productClient;
        this.recommendationClient = recommendationClient;
        this.reviewClient = reviewClient;
    }

    // ============================================================
    // 🔹 GET — Construire l'agrégat complet à partir des microservices
    // ============================================================
    public ProductAggregate buildAggregate(int productId) {
        log.info("🔄 Intégration : appel des microservices pour productId={}", productId);

        // 1️⃣ Produit
        var p = productClient.get(productId);
        var productSummary = new ProductAggregate.ProductSummary(
                p.getProductId(),
                p.getName(),
                p.getWeight()
        );

        // 2️⃣ Recommandations
        var recommendations = recommendationClient.byProduct(productId)
                .stream()
                .map(r -> new ProductAggregate.RecommendationSummary(
                        r.recommendationId(),
                        r.author(),
                        r.rate(),
                        r.content()))
                .collect(Collectors.toList());

        // 3️⃣ Avis
        var reviews = reviewClient.byProduct(productId)
                .stream()
                .map(r -> new ProductAggregate.ReviewSummary(
                        r.reviewId(),
                        r.author(),
                        r.subject(),
                        r.content()))
                .collect(Collectors.toList());

        log.info("✅ Données agrégées : produit={}, {} recommandations, {} avis",
                productSummary.getProductId(), recommendations.size(), reviews.size());

        return new ProductAggregate(productSummary, reviews, recommendations);
    }

    // ============================================================
    // 🔹 POST — Création du produit + ses reviews + recommandations
    // ============================================================
    public void createProduct(ProductAggregate.ProductSummary product) {
        log.info("📦 [Integration] Création du produit : {} (poids={})", product.getName(), product.getWeight());
        productClient.create(product); // ✅ appel réel du microservice
        log.info("✅ Produit créé via Product-Service");
    }

    // ============================================================
    // 🔹 PUT — Mise à jour du produit existant
    // ============================================================
    public void updateProduct(int id, ProductAggregate.ProductSummary product) {
        log.info("✏️ [Integration] Mise à jour du produit id={} (poids={})", id, product.getWeight());
        productClient.update(id, product); // ✅ appel réel du microservice
        log.info("✅ Produit mis à jour via Product-Service");
    }

    // ============================================================
    // 🔹 POST — Création d'un avis (Review)
    // ============================================================
    public void createReview(int productId, ProductAggregate.ReviewSummary review) {
        log.info("📝 [Integration] Création d’un avis pour productId={} par {}", productId, review.getAuthor());
        reviewClient.create(productId, review); // ✅ appel réel
    }

    // ============================================================
    // 🔹 POST — Création d'une recommandation
    // ============================================================
    public void createRecommendation(int productId, ProductAggregate.RecommendationSummary recommendation) {
        log.info("💡 [Integration] Création d’une recommandation pour productId={} par {}", productId, recommendation.getAuthor());
        recommendationClient.create(productId, recommendation); // ✅ appel réel
    }

}
