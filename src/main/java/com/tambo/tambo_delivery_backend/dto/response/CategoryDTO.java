package com.tambo.tambo_delivery_backend.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private UUID id;
    private String name;
    private String description;
    private String imageUrl;
    private List<CategoryTypeDTO> categoryTypes; // Si quieres que el cliente vea los tipos de categoría cuando consulta una categoría

}
