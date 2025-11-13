package com.tambo.tambo_delivery_backend.dto.request;

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
public class CategoryRequestDTO {

    private String name;
    private String description;
    private String imageUrl;
    private List<UUID> categoryTypeIds;

}
