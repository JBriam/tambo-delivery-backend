package com.tambo.tambo_delivery_backend.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tambo.tambo_delivery_backend.dto.request.CategoryTypeRequestDTO;
import com.tambo.tambo_delivery_backend.dto.response.CategoryTypeDTO;
import com.tambo.tambo_delivery_backend.entities.Category;
import com.tambo.tambo_delivery_backend.entities.CategoryType;
import com.tambo.tambo_delivery_backend.mapper.CategoryTypeMapper;
import com.tambo.tambo_delivery_backend.repositories.CategoryRepository;
import com.tambo.tambo_delivery_backend.repositories.CategoryTypeRepository;

@Service
public class CategoryTypeService {

    @Autowired
    private CategoryTypeRepository categoryTypeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Obtener todos los tipos de categoría
    public List<CategoryTypeDTO> getAllCategoryTypes() {
        return categoryTypeRepository.findAll().stream()
                .map(CategoryTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Obtener tipos de categoría por ID de categoría
    public List<CategoryTypeDTO> getCategoryTypesByCategoryId(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Categoría con id " + categoryId + " no encontrada");
        }
        return categoryTypeRepository.findByCategoryId(categoryId).stream()
                .map(CategoryTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Obtener un tipo de categoría por ID
    public CategoryTypeDTO getCategoryTypeById(UUID id) {
        CategoryType categoryType = categoryTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de categoría con id " + id + " no encontrado"));
        return CategoryTypeMapper.toDTO(categoryType);
    }

    // Crear un tipo de categoría
    @Transactional
    public CategoryTypeDTO createCategoryType(CategoryTypeRequestDTO dto) {
        // Verificar que la categoría existe
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría con id: " + dto.getCategoryId() + " no encontrada"));

        // Crear el tipo de categoría
        CategoryType categoryType = CategoryTypeMapper.toEntity(dto, category);
        CategoryType saved = categoryTypeRepository.save(categoryType);
        
        return CategoryTypeMapper.toDTO(saved);
    }

    // Actualizar un tipo de categoría
    @Transactional
    public CategoryTypeDTO updateCategoryType(UUID id, CategoryTypeRequestDTO dto) {
        CategoryType existing = categoryTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de categoría con id " + id + " no encontrado"));

        // Si se cambió la categoría padre, verificar que existe
        if (dto.getCategoryId() != null && !dto.getCategoryId().equals(existing.getCategory().getId())) {
            Category newCategory = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría con id " + dto.getCategoryId() + " no encontrada"));
            existing.setCategory(newCategory);
        }

        // Actualizar campos
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());

        CategoryType updated = categoryTypeRepository.save(existing);
        return CategoryTypeMapper.toDTO(updated);
    }

    // Eliminar un tipo de categoría
    @Transactional
    public void deleteCategoryType(UUID id) {
        if (!categoryTypeRepository.existsById(id)) {
            throw new RuntimeException("Tipo de categoría con id " + id + " no encontrado");
        }
        categoryTypeRepository.deleteById(id);
    }
}
