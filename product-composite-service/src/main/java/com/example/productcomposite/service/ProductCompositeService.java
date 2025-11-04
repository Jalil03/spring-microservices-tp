package com.example.productcomposite.service;

import com.example.productcomposite.api.ProductAggregate;
import com.example.productcomposite.clients.ProductClient;
import com.example.productcomposite.clients.RecommendationClient;
import com.example.productcomposite.clients.ReviewClient;
import com.example.productcomposite.integration.ProductCompositeIntegration;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCompositeService {

    private static final Logger log = LoggerFactory.getLogger(ProductCompositeService.class);

    private final ProductClient productClient;
    private final ReviewClient reviewClient;
    private final RecommendationClient recommendationClient;
    private final ProductCompositeIntegration integration;

    public ProductCompositeService(ProductClient productClient,
                                   ReviewClient reviewClient,
                                   RecommendationClient recommendationClient,
                                   ProductCompositeIntegration integration) {
        this.productClient = productClient;
        this.reviewClient = reviewClient;
        this.recommendationClient = recommendationClient;
        this.integration = integration;
    }

    // ========================================================
    // 🔹 1. GET — Agrégation des données
    // ========================================================
    @CircuitBreaker(name = "productCompositeCB", fallbackMethod = "fallbackGetAggregate")
    public ProductAggregate getAggregate(int id) {
        log.info("➡️ Début de l’agrégation pour productId={}", id);

        try {
            // 🟢 Appel Product-Service
            log.info("➡️ Appel Product-Service via LoadBalancer pour productId={}", id);
            var p = productClient.get(id);

            // ✅ Vérifie si le produit existe
            if (p == null || p.getName() == null || p.getName().equalsIgnoreCase("Not Found")) {
                log.warn("⚠️ Produit introuvable pour productId={}", id);
                return new ProductAggregate(
                        new ProductAggregate.ProductSummary(id, "Produit introuvable", 0),
                        Collections.emptyList(),
                        Collections.emptyList()
                );
            }

            log.info("✅ Product récupéré : {} (poids = {})", p.getName(), p.getWeight());

            // 🟡 Appel Review-Service
            List<ProductAggregate.ReviewSummary> reviews = Collections.emptyList();
            try {
                log.info("➡️ Appel Review-Service via LoadBalancer pour productId={}", id);
                reviews = reviewClient.byProduct(id).stream()
                        .map(r -> new ProductAggregate.ReviewSummary(
                                r.reviewId(), r.author(), r.subject(), r.content()))
                        .collect(Collectors.toList());
                log.info("📝 Nombre d’avis récupérés : {}", reviews.size());
            } catch (FeignException e) {
                log.warn("⚠️ Review-Service indisponible : {}", e.getMessage());
            }

            // 🔵 Appel Recommendation-Service
            List<ProductAggregate.RecommendationSummary> recos = Collections.emptyList();
            try {
                log.info("➡️ Appel Recommendation-Service via LoadBalancer pour productId={}", id);
                recos = recommendationClient.byProduct(id).stream()
                        .map(x -> new ProductAggregate.RecommendationSummary(
                                x.recommendationId(), x.author(), x.rate(), x.content()))
                        .collect(Collectors.toList());
                log.info("💡 Nombre de recommandations récupérées : {}", recos.size());
            } catch (FeignException e) {
                log.warn("⚠️ Recommendation-Service indisponible : {}", e.getMessage());
            }

            // 🧩 Construction de l’agrégat final
            var ps = new ProductAggregate.ProductSummary(p.getProductId(), p.getName(), p.getWeight());
            var aggregate = new ProductAggregate(ps, reviews, recos);

            log.info("✅ Agrégat final généré avec succès pour productId={}", id);
            return aggregate;

        } catch (Exception e) {
            log.error("❌ Erreur lors de l’agrégation du produit {} : {}", id, e.getMessage());
            return new ProductAggregate(
                    new ProductAggregate.ProductSummary(id, "Erreur interne (fallback local)", 0),
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }
    }

    // ========================================================
    // 🔹 2. POST — Création d’un nouveau produit
    // ========================================================
    public void createAggregate(ProductAggregate product) {
        var prod = product.getProduct();
        log.info("📦 Création d’un nouveau produit (id={}, nom={}, poids={})",
                prod.getProductId(), prod.getName(), prod.getWeight());

        if (prod.getWeight() > 100) {
            throw new IllegalArgumentException("❌ Le poids du produit ne doit pas dépasser 100 !");
        }

        try {
            // Appel du Product-Service pour créer le produit
            integration.createProduct(prod);
            log.info("✅ Produit créé dans Product-Service");

            // Optionnel : créer aussi les reviews et recommandations
            if (product.getReviews() != null) {
                product.getReviews().forEach(review ->
                        integration.createReview(prod.getProductId(), review));
                log.info("📝 Reviews créées : {}", product.getReviews().size());
            }

            if (product.getRecommendations() != null) {
                product.getRecommendations().forEach(reco ->
                        integration.createRecommendation(prod.getProductId(), reco));
                log.info("💡 Recommandations créées : {}", product.getRecommendations().size());
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la création du produit composite : {}", e.getMessage());
        }
    }

    // ========================================================
    // 🔹 3. PUT — Mise à jour d’un produit existant
    // ========================================================
    public void updateAggregate(int productId, ProductAggregate product) {
        var prod = product.getProduct();
        log.info("✏️ Mise à jour du produit id={} (nouveau poids={})", productId, prod.getWeight());

        if (prod.getWeight() > 100) {
            throw new IllegalArgumentException("❌ Le poids du produit ne doit pas dépasser 100 !");
        }

        try {
            // Appel du Product-Service pour mise à jour
            integration.updateProduct(productId, prod);
            log.info("✅ Produit mis à jour dans Product-Service");

        } catch (Exception e) {
            log.error("❌ Erreur lors de la mise à jour du produit composite : {}", e.getMessage());
        }
    }

    // ========================================================
    // 🔹 4. Méthode fallback (Resilience4J)
    // ========================================================
    public ProductAggregate fallbackGetAggregate(int id, Throwable cause) {
        log.warn("⚠️ Fallback activé pour productId={} - cause: {}", id, cause.toString());

        var ps = new ProductAggregate.ProductSummary(
                id, "Produit temporairement indisponible (fallback)", 0);

        return new ProductAggregate(ps, Collections.emptyList(), Collections.emptyList());
    }
}
