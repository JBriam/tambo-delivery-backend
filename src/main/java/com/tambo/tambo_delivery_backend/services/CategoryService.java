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
import com.tambo.tambo_delivery_backend.entities.CategoryType;
import com.tambo.tambo_delivery_backend.mapper.CategoryMapper;
import com.tambo.tambo_delivery_backend.repositories.CategoryRepository;
import com.tambo.tambo_delivery_backend.repositories.CategoryTypeRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryTypeRepository categoryTypeRepository;

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
    public CategoryDTO createCategory(CategoryRequestDTO request) {
        // Usar el mapper con el repositorio
        Category category = CategoryMapper.toEntity(request, categoryTypeRepository);

        Category saved = categoryRepository.save(category);
        return CategoryMapper.toDTO(saved);
    }

    // Actualizar una categoria por ID
    @Transactional
    public CategoryDTO updateCategory(UUID id, CategoryRequestDTO categoryData) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));

        // Actualizar datos básicos
        existing.setName(categoryData.getName());
        existing.setDescription(categoryData.getDescription());
        existing.setImageUrl(categoryData.getImageUrl());

        // ✅ Actualizar tipos sin eliminar los antiguos
        List<UUID> newTypeIds = categoryData.getCategoryTypeIds();
        if (newTypeIds != null && !newTypeIds.isEmpty()) {
            List<CategoryType> newTypes = categoryTypeRepository.findAllById(newTypeIds);

            for (CategoryType newType : newTypes) {
                boolean exists = existing.getCategoryTypes().stream()
                        .anyMatch(t -> t.getId().equals(newType.getId()));
                if (!exists) {
                    existing.getCategoryTypes().add(newType);
                }
            }
        }

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
