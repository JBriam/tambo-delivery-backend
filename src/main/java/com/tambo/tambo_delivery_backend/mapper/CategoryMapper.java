package com.tambo.tambo_delivery_backend.mapper;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tambo.tambo_delivery_backend.dto.CategoryDTO;
import com.tambo.tambo_delivery_backend.dto.CategoryRequestDTO;
import com.tambo.tambo_delivery_backend.dto.CategoryTypeDTO;
import com.tambo.tambo_delivery_backend.entities.Category;
import com.tambo.tambo_delivery_backend.entities.CategoryType;

@Component
public class CategoryMapper {

    public static Category toEntity(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setImageUrl(dto.getImageUrl());
        category.setDescription(dto.getDescription());

        // Mapear los tipos de categoría si existen
        if (dto.getCategoryTypes() != null && !dto.getCategoryTypes().isEmpty()) {
            var types = dto.getCategoryTypes().stream()
                    .map(typeDTO -> {
                        CategoryType type = new CategoryType();
                        type.setName(typeDTO.getName());
                        type.setDescription(typeDTO.getDescription());
                        type.setCategory(category); // Establecer la relación bidireccional
                        return type;
                    })
                    .collect(Collectors.toList());
            category.setCategoryTypes(types);
        }

        return category;
    }

    public static CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .description(category.getDescription())
                .categoryTypes(
                    category.getCategoryTypes() != null 
                        ? category.getCategoryTypes().stream()
                            .map(type -> CategoryTypeDTO.builder()
                                .id(type.getId())
                                .name(type.getName())
                                .description(type.getDescription())
                                .build())
                            .collect(Collectors.toList())
                        : Collections.emptyList()
                )
                .build();
    }

}
