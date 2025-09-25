package com.tambo.tambo_delivery_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTypeRequestDTO {
    private String name;
    private String code;
    private String description;
}
