package com.example.recommendationservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive
    private Long productId;

    @NotBlank
    private String author;

    @NotBlank
    private String content;

    @PositiveOrZero
    @Max(100)
    private int rate;  // pourcentage entre 0 et 100
}
