package com.tambo.tambo_delivery_backend.mapper;

import com.tambo.tambo_delivery_backend.entities.ProductSection;
import com.tambo.tambo_delivery_backend.dto.response.ProductSectionDTO;
import com.tambo.tambo_delivery_backend.entities.Category;

public class ProductSectionMapper {

    public static ProductSectionDTO toDTO(ProductSection entity) {
        if (entity == null)
            return null;
        ProductSectionDTO dto = new ProductSectionDTO();
        dto.setId(entity.getId());
        dto.setCategoryId(entity.getCategory() != null
                ? entity.getCategory().getId()
                : null);
        dto.setMaxProducts(entity.getMaxProducts());
        dto.setPosition(entity.getPosition());
        return dto;
    }

    public static ProductSection toEntity(ProductSectionDTO dto) {
        if (dto == null)
            return null;
        ProductSection entity = new ProductSection();
        entity.setId(dto.getId());
        if (dto.getCategoryId() != null) {
            Category cat = new Category();
            cat.setId(dto.getCategoryId());
            entity.setCategory(cat);
        }
        entity.setMaxProducts(dto.getMaxProducts() != null
                ? dto.getMaxProducts()
                : 8);
        entity.setPosition(dto.getPosition());
        return entity;
    }
}
