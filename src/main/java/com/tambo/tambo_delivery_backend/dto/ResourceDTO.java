package com.tambo.tambo_delivery_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ResourceDTO {
    private UUID id;
    private String name;
    private String url;
    private Boolean isPrimary;
    private String type;
}
