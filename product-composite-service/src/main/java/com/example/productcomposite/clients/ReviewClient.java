package com.example.productcomposite.clients;

import com.example.productcomposite.api.ProductAggregate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@FeignClient(
        name = "REVIEW-SERVICE",
        configuration = com.example.productcomposite.config.FeignConfig.class,
        fallback = ReviewClientFallback.class
)
public interface ReviewClient {

    @GetMapping("/reviews/product/{productId}")
    List<ReviewDTO> byProduct(@PathVariable int productId);

    @PostMapping("/reviews/product/{productId}")
    void create(@PathVariable int productId, @RequestBody ProductAggregate.ReviewSummary review);

    record ReviewDTO(int reviewId, int productId, String author, String subject, String content) {}
}


