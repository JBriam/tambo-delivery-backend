package com.tambo.tambo_delivery_backend.dto;

// com.tambo.tambo_delivery_backend.dto.SliderImageDTO

import lombok.Data;
import java.util.UUID;

@Data
public class SliderImageDTO {
    private UUID id;
    private String imageUrl;
    private String redirectUrl;
    private Integer position;
}
