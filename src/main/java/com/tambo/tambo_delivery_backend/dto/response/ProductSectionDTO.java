package com.tambo.tambo_delivery_backend.dto.response;

// com.tambo.tambo_delivery_backend.dto.ProductSectionDTO

import lombok.Data;
import java.util.UUID;

@Data
public class ProductSectionDTO {
    private UUID id;
    private UUID categoryId;
    private Integer maxProducts;
    private Integer position;
}
