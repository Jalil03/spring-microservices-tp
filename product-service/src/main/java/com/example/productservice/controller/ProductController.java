package com.example.productservice.controller;

import com.example.productservice.dto.ProductDTO;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository repository;

    @Value("${server.port}")
    private String port;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    // ✅ POST : créer un produit avec validation DTO
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody ProductDTO dto) {
        log.info("🟢 Création d’un produit '{}' (poids={}) sur port={}", dto.getName(), dto.getWeight(), port);

        // ✅ Conversion DTO -> Entity
        Product product = new Product(dto.getProductId(), dto.getName(), dto.getWeight());
        return repository.save(product);
    }

    // ✅ GET : récupérer tous les produits
    @GetMapping
    public List<Product> getAll() {
        log.info("📦 Instance Product-Service (port={}) - récupération de tous les produits", port);
        return repository.findAll();
    }

    // ✅ GET : récupérer un produit par ID
    @GetMapping("/{id}")
    public Product getById(@PathVariable int id) {
        log.info("📦 Instance Product-Service (port={}) traite la requête pour productId={}", port, id);
        return repository.findById(id).orElse(new Product(id, "Not Found", 0));
    }

    // ✅ DELETE : supprimer un produit
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.warn("🗑️ Suppression du produit={} sur port={}", id, port);
        repository.deleteById(id);
    }

    // ✅ PUT : mise à jour avec DTO
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Product updateProduct(@PathVariable int id, @Valid @RequestBody ProductDTO dto) {
        log.info("✏️ Mise à jour du produit id={} (nouveau nom='{}', poids={}) sur port={}",
                id, dto.getName(), dto.getWeight(), port);

        return repository.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setWeight(dto.getWeight());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Produit non trouvé pour id=" + id));
    }
}
