package com.tambo.tambo_delivery_backend.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tambo.tambo_delivery_backend.dto.request.CategoryRequestDTO;
import com.tambo.tambo_delivery_backend.dto.response.CategoryDTO;
import com.tambo.tambo_delivery_backend.dto.response.CategoryTypeDTO;
import com.tambo.tambo_delivery_backend.entities.Category;
import com.tambo.tambo_delivery_backend.entities.CategoryType;
import com.tambo.tambo_delivery_backend.repositories.CategoryTypeRepository;

@Component
public class CategoryMapper {

    public static Category toEntity(CategoryRequestDTO dto, CategoryTypeRepository categoryTypeRepository) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setImageUrl(dto.getImageUrl());
        category.setDescription(dto.getDescription());

        // Mapear los tipos de categoría si existen (ahora son IDs, no objetos
        // completos)
        if (dto.getCategoryTypeIds() != null && !dto.getCategoryTypeIds().isEmpty()) {
            List<CategoryType> types = categoryTypeRepository.findAllById(dto.getCategoryTypeIds());
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
                                : Collections.emptyList())
                .build();
    }

}
