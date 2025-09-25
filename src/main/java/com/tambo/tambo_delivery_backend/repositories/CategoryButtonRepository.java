package com.tambo.tambo_delivery_backend.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tambo.tambo_delivery_backend.entities.CategoryButton;

public interface CategoryButtonRepository extends JpaRepository<CategoryButton, UUID> {
    List<CategoryButton> findAllByOrderByPositionAsc();
}