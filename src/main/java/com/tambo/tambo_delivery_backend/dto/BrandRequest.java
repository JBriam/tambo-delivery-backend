package com.tambo.tambo_delivery_backend.dto;

import lombok.Data;

@Data
public class BrandRequest {
    private String name;
    private String description;
    private String imageUrl;
}
