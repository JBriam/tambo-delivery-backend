package com.tambo.tambo_delivery_backend.dto;

// com.Login.Backend.dto.CategoryButtonDTO

import lombok.Data;
import java.util.UUID;

@Data
public class CategoryButtonDTO {
    private UUID id;
    private UUID categoryId;
    private Integer position;
}
