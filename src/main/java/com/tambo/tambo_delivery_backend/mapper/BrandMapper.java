package com.tambo.tambo_delivery_backend.mapper;

import org.springframework.stereotype.Component;

import com.tambo.tambo_delivery_backend.dto.request.BrandRequest;
import com.tambo.tambo_delivery_backend.dto.response.BrandDTO;
import com.tambo.tambo_delivery_backend.entities.Brand;

@Component
public class BrandMapper {

    public static Brand toEntity(BrandRequest request) {
        return Brand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();
    }

    public static BrandDTO toDTO(Brand brand) {
        return BrandDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .imageUrl(brand.getImageUrl())
                .build();
    }
}
