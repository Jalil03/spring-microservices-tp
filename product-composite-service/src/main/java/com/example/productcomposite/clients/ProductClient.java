package com.example.productcomposite.clients;

import com.example.productcomposite.api.ProductAggregate.ProductSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @GetMapping("/product/{id}")
    ProductSummary get(@PathVariable int id);

    @PostMapping("/product")
    void create(@RequestBody ProductSummary product);

    @PutMapping("/product/{id}")
    void update(@PathVariable int id, @RequestBody ProductSummary product);
}
