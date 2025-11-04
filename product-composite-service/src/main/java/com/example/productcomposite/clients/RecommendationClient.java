package com.example.productcomposite.clients;

import com.example.productcomposite.api.ProductAggregate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "RECOMMENDATION-SERVICE")
public interface RecommendationClient {

    @GetMapping("/recommendation")
    List<RecoDTO> byProduct(@RequestParam int productId);

    @PostMapping("/recommendation")
    void create(@RequestParam int productId, @RequestBody ProductAggregate.RecommendationSummary reco);

    record RecoDTO(int productId, int recommendationId, String author, int rate, String content) {}
}
