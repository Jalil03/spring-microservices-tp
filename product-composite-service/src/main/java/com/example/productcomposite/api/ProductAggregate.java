// com.example.productcomposite.api.ProductAggregate.java
package com.example.productcomposite.api;
import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class ProductAggregate {
    private ProductSummary product;
    private List<ReviewSummary> reviews;
    private List<RecommendationSummary> recommendations;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ProductSummary { int productId; String name; int weight; }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ReviewSummary { int reviewId; String author; String subject; String content; }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RecommendationSummary { int recommendationId; String author; int rate; String content; }
}
