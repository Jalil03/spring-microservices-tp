package com.example.reviewservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "review_id")
    private Integer reviewId; // ⚙️ Géré automatiquement par la BD

    @Positive(message = "Le productId doit être positif")
    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @NotBlank(message = "L'auteur ne peut pas être vide")
    @Column(nullable = false)
    private String author;

    @NotBlank(message = "Le sujet ne peut pas être vide")
    @Column(nullable = false)
    private String subject;

    @NotBlank(message = "Le contenu ne peut pas être vide")
    @Column(nullable = false)
    private String content;
}
