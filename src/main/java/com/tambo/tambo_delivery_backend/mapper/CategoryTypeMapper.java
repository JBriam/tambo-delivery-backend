package com.tambo.tambo_delivery_backend.mapper;

import org.springframework.stereotype.Component;

import com.tambo.tambo_delivery_backend.dto.request.CategoryTypeRequestDTO;
import com.tambo.tambo_delivery_backend.dto.response.CategoryTypeDTO;
import com.tambo.tambo_delivery_backend.entities.Category;
import com.tambo.tambo_delivery_backend.entities.CategoryType;

@Component
public class CategoryTypeMapper {

    public static CategoryType toEntity(CategoryTypeRequestDTO dto, Category category) {
        CategoryType categoryType = new CategoryType();
        categoryType.setName(dto.getName());
        categoryType.setDescription(dto.getDescription());
        categoryType.setCategory(category);

        return categoryType;
    }

    public static CategoryTypeDTO toDTO(CategoryType categoryType) {
        return CategoryTypeDTO.builder()
                .id(categoryType.getId())
                .name(categoryType.getName())
                .description(categoryType.getDescription())
                .build();
    }
}
