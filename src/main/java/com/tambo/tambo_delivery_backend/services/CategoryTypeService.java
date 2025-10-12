package com.tambo.tambo_delivery_backend.services;

import com.tambo.tambo_delivery_backend.dto.CategoryTypeDTO;
import com.tambo.tambo_delivery_backend.dto.CategoryTypeRequestDTO;
import com.tambo.tambo_delivery_backend.entities.Category;
import com.tambo.tambo_delivery_backend.entities.CategoryType;
import com.tambo.tambo_delivery_backend.mapper.CategoryTypeMapper;
import com.tambo.tambo_delivery_backend.repositories.CategoryRepository;
import com.tambo.tambo_delivery_backend.repositories.CategoryTypeRepositoty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryTypeService {

    @Autowired
    private CategoryTypeRepositoty categoryTypeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Obtener todos los tipos de categoría
    public List<CategoryTypeDTO> getAllCategoryTypes() {
        return categoryTypeRepository.findAll().stream()
                .map(CategoryTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Obtener tipos de categoría por categoría padre
    public List<CategoryTypeDTO> getCategoryTypesByCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        return category.getCategoryTypes().stream()
                .map(CategoryTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Obtener un tipo de categoría por ID
    public CategoryTypeDTO getCategoryTypeById(UUID id) {
        CategoryType categoryType = categoryTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CategoryType not found"));
        return CategoryTypeMapper.toDTO(categoryType);
    }

    // Crear un tipo de categoría
    @Transactional
    public CategoryTypeDTO createCategoryType(UUID categoryId, CategoryTypeRequestDTO dto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        CategoryType categoryType = CategoryTypeMapper.toEntity(dto, category);
        CategoryType saved = categoryTypeRepository.save(categoryType);
        return CategoryTypeMapper.toDTO(saved);
    }

    // Actualizar un tipo de categoría
    @Transactional
    public CategoryTypeDTO updateCategoryType(UUID id, CategoryTypeRequestDTO dto) {
        CategoryType existing = categoryTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CategoryType not found"));

        existing.setName(dto.getName());
        existing.setCode(dto.getCode());
        existing.setDescription(dto.getDescription());

        CategoryType updated = categoryTypeRepository.save(existing);
        return CategoryTypeMapper.toDTO(updated);
    }

    // Eliminar un tipo de categoría
    @Transactional
    public void deleteCategoryType(UUID id) {
        CategoryType categoryType = categoryTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CategoryType not found"));
        
        categoryTypeRepository.delete(categoryType);
    }
}