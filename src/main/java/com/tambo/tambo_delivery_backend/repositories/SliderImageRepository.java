package com.tambo.tambo_delivery_backend.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tambo.tambo_delivery_backend.entities.SliderImage;

public interface SliderImageRepository extends JpaRepository<SliderImage, UUID> {
    List<SliderImage> findAllByOrderByPositionAsc();
}