package com.example.productcomposite.clients;

import com.example.productcomposite.api.ProductAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ReviewClientFallback implements ReviewClient {

    private static final Logger log = LoggerFactory.getLogger(ReviewClientFallback.class);

    @Override
    public List<ReviewDTO> byProduct(int productId) {
        log.warn("⚠️ Fallback ReviewClient activé pour productId={}", productId);
        return Collections.emptyList();
    }

    @Override
    public void create(int productId, ProductAggregate.ReviewSummary review) {
        log.warn("⚠️ Fallback ReviewClient → Impossible de créer un avis pour productId={} (service indisponible)", productId);
    }
}
