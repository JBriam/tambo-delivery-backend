package com.tambo.tambo_delivery_backend.entities;

// 2) Botones de categoría (slider de categorías en la home)

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "category_buttons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryButton {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Integer position; // orden en el slider de categorías
}
