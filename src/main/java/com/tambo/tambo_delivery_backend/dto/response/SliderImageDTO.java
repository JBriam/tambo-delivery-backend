package com.tambo.tambo_delivery_backend.dto.response;

// com.tambo.tambo_delivery_backend.dto.SliderImageDTO

import java.util.UUID;

import lombok.Data;

@Data
public class SliderImageDTO {
    private UUID id;
    private String imageUrl;
    private String redirectUrl;
    private Integer position;
}
