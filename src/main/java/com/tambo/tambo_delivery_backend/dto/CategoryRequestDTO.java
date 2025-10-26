package com.tambo.tambo_delivery_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDTO {

    private String name;
    private String description;
    private String imageUrl;
    private List<CategoryTypeRequestDTO> categoryTypes; // Lista de tipos de categoría a crear

}
