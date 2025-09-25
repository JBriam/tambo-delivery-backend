package com.tambo.tambo_delivery_backend.repositories;

import com.tambo.tambo_delivery_backend.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
}
