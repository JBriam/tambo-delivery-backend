package com.tambo.tambo_delivery_backend.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tambo.tambo_delivery_backend.entities.CategoryType;

public interface CategoryTypeRepositoty extends JpaRepository<CategoryType, UUID> {

    Optional<CategoryType> findById(UUID id);
}
