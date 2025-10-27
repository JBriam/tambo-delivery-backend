package com.tambo.tambo_delivery_backend.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourceDTO {
    private UUID id;
    private String name;
    private String url;
    private Boolean isPrimary;
    private String type;
}
