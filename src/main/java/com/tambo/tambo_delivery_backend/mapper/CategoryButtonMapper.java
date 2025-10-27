package com.tambo.tambo_delivery_backend.mapper;
// com.tambo.tambo_delivery_backend.mapper.CategoryButtonMapper

import com.tambo.tambo_delivery_backend.entities.CategoryButton;
import com.tambo.tambo_delivery_backend.dto.response.CategoryButtonDTO;
import com.tambo.tambo_delivery_backend.entities.Category;

public class CategoryButtonMapper {

    public static CategoryButtonDTO toDTO(CategoryButton entity) {
        if (entity == null)
            return null;
        CategoryButtonDTO dto = new CategoryButtonDTO();
        dto.setId(entity.getId());
        dto.setCategoryId(entity.getCategory() != null
                ? entity.getCategory().getId()
                : null);
        dto.setPosition(entity.getPosition());
        return dto;
    }

    public static CategoryButton toEntity(CategoryButtonDTO dto) {
        if (dto == null)
            return null;
        CategoryButton entity = new CategoryButton();
        entity.setId(dto.getId());
        // Solo asignamos la referencia mínima a Category
        if (dto.getCategoryId() != null) {
            Category cat = new Category();
            cat.setId(dto.getCategoryId());
            entity.setCategory(cat);
        }
        entity.setPosition(dto.getPosition());
        return entity;
    }
}
