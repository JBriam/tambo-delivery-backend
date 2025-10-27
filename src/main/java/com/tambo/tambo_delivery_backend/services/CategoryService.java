package com.tambo.tambo_delivery_backend.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tambo.tambo_delivery_backend.dto.request.CategoryRequestDTO;
import com.tambo.tambo_delivery_backend.dto.response.CategoryDTO;
import com.tambo.tambo_delivery_backend.entities.Category;
import com.tambo.tambo_delivery_backend.mapper.CategoryMapper;
import com.tambo.tambo_delivery_backend.repositories.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Obtener todas las categorias
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Obtener una categoria por ID
    public CategoryDTO getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return CategoryMapper.toDTO(category);
    }

    // Crear una categoria
    @Transactional
    public CategoryDTO createCategory(CategoryRequestDTO dto) {
        Category category = CategoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toDTO(saved);
    }

    // Actualizar una categoria por ID
    @Transactional
    public CategoryDTO updateCategory(UUID id, CategoryRequestDTO dto) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Actualizar campos básicos
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setImageUrl(dto.getImageUrl());

        // Actualizar tipos de categoría
        if (dto.getCategoryTypes() != null) {
            // Limpiar los tipos existentes
            existing.getCategoryTypes().clear();

            // Agregar los nuevos tipos
            dto.getCategoryTypes().forEach(typeDTO -> {
                var type = new com.tambo.tambo_delivery_backend.entities.CategoryType();
                type.setName(typeDTO.getName());
                type.setDescription(typeDTO.getDescription());
                type.setCategory(existing);
                existing.getCategoryTypes().add(type);
            });
        }

        // Guardar cambios
        Category updated = categoryRepository.save(existing);
        return CategoryMapper.toDTO(updated);
    }

    // Eliminar una categoria por ID
    public void deleteCategory(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }
        categoryRepository.deleteById(id);
    }
}
