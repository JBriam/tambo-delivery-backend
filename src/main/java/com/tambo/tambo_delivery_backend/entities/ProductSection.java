package com.tambo.tambo_delivery_backend.entities;

// 3) Secciones de productos por categoría

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSection {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    @Builder.Default
    private Integer maxProducts = 8; // máximo de productos a mostrar

    @Column(nullable = false)
    private Integer position; // posición/orden de la sección
}
