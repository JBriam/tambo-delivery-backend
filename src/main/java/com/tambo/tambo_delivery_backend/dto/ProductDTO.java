package com.tambo.tambo_delivery_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProductDTO {
    private UUID id;
    private String thumbnail;
    private String slug;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPercentage;
    private BigDecimal discountedPrice;
    private Integer stock;
    private BrandDTO brand;
    private Float rating;
    private Boolean isNewArrival;
    private Boolean isActive;
    private Date createdAt;
    private Date updatedAt;
    private CategoryDTO category;
    private CategoryTypeDTO categoryType;
    private List<ResourceDTO> resources;
    private List<DiscountDTO> discounts;
}
