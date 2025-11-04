package com.example.productservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    @NotNull(message = "L'ID du produit est obligatoire")
    private Integer productId;

    @NotBlank(message = "Le nom du produit est obligatoire")
    private String name;

    @Min(value = 1, message = "Le poids doit être positif")
    @Max(value = 100, message = "Le poids ne doit pas dépasser 100")
    private int weight;
}
