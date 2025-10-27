package com.tambo.tambo_delivery_backend.dto.response;

// com.Login.Backend.dto.CategoryButtonDTO

import java.util.UUID;

import lombok.Data;

@Data
public class CategoryButtonDTO {
    private UUID id;
    private UUID categoryId;
    private Integer position;
}
