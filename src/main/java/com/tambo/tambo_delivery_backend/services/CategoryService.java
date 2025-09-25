package com.tambo.tambo_delivery_backend.services;

import com.tambo.tambo_delivery_backend.dto.CategoryDTO;
import com.tambo.tambo_delivery_backend.dto.CategoryRequestDTO;
import com.tambo.tambo_delivery_backend.entities.Category;
import com.tambo.tambo_delivery_backend.entities.CategoryType;
import com.tambo.tambo_delivery_backend.mapper.CategoryMapper;
import com.tambo.tambo_delivery_backend.repositories.CategoryRepository;
import com.tambo.tambo_delivery_backend.repositories.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

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

        // 1) Desvincula productos de los tipos que vamos a eliminar
        for (CategoryType oldType : existing.getCategoryTypes()) {
            productRepository.findAllByCategoryType(oldType)
                    .forEach(prod -> {
                        prod.setCategoryType(null);
                        productRepository.save(prod);
                    });
        }

        existing.setName(dto.getName());
        existing.setCode(dto.getCode());
        existing.setDescription(dto.getDescription());

        // 2) Ahora sí remueve los tipos antiguos
        existing.getCategoryTypes().clear();

        // 3) Agrega los nuevos tipos
        dto.getCategoryTypes().forEach(typeDTO -> {
            var type = new com.tambo.tambo_delivery_backend.entities.CategoryType();
            type.setName(typeDTO.getName());
            type.setCode(typeDTO.getCode());
            type.setDescription(typeDTO.getDescription());
            type.setCategory(existing);
            existing.getCategoryTypes().add(type);
        });

        // 4) Guarda la categoría con sus tipos actualizados
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
