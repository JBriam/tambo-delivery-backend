package com.tambo.tambo_delivery_backend.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tambo.tambo_delivery_backend.entities.CategoryType;

@Repository
public interface CategoryTypeRepository extends JpaRepository<CategoryType, UUID> {
    
    // Buscar todos los tipos de categoría por ID de categoría
    List<CategoryType> findByCategoryId(UUID categoryId);
    
    // Verificar si existe un tipo de categoría con un nombre específico en una categoría
    boolean existsByNameAndCategoryId(String name, UUID categoryId);
}
