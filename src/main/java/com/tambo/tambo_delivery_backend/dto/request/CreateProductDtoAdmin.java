package com.tambo.tambo_delivery_backend.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreateProductDtoAdmin {
    private String slug;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private UUID brandId;
    private Boolean isNewArrival;
    private Boolean isActive;
    private UUID categoryId;
    private UUID categoryTypeId;
    private List<ResourceRequestDTO> resources;
    private List<UUID> discountIds;
}
