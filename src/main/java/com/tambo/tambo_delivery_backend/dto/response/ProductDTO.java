package com.tambo.tambo_delivery_backend.dto.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

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
