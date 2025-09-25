package com.tambo.tambo_delivery_backend.repositories;

import com.tambo.tambo_delivery_backend.entities.CategoryType;
import com.tambo.tambo_delivery_backend.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    List<Product> findAllByIsActiveTrue();

    Optional<Product> findBySlugAndIsActiveTrue(String slug);

    boolean existsBySlug(String slug);

    List<Product> findAllByCategoryType(CategoryType categoryType);

    List<Product> findByCategoryId(UUID categoryId);

}
