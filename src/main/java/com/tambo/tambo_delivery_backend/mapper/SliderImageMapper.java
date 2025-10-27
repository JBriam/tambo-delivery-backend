package com.tambo.tambo_delivery_backend.mapper;
// com.tambo.tambo_delivery_backend.mapper.SliderImageMapper

import com.tambo.tambo_delivery_backend.dto.response.SliderImageDTO;
import com.tambo.tambo_delivery_backend.entities.SliderImage;

public class SliderImageMapper {

    public static SliderImageDTO toDTO(SliderImage entity) {
        if (entity == null)
            return null;
        SliderImageDTO dto = new SliderImageDTO();
        dto.setId(entity.getId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setRedirectUrl(entity.getRedirectUrl());
        dto.setPosition(entity.getPosition());
        return dto;
    }

    public static SliderImage toEntity(SliderImageDTO dto) {
        if (dto == null)
            return null;
        SliderImage entity = new SliderImage();
        entity.setId(dto.getId()); // si es creación, dto.getId() puede ser null
        entity.setImageUrl(dto.getImageUrl());
        entity.setRedirectUrl(dto.getRedirectUrl());
        entity.setPosition(dto.getPosition());
        return entity;
    }
}
