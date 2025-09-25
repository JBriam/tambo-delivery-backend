package com.tambo.tambo_delivery_backend.entities;

// 1) Imagenes del slider

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "slider_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SliderImage {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String redirectUrl;

    @Column(nullable = false)
    private Integer position; // orden en el slider
}
