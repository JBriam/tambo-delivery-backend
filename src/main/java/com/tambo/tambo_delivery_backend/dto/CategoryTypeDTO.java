package com.tambo.tambo_delivery_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTypeDTO {
    private UUID id;
    private String name;
    private String code;
    private String description;
}
