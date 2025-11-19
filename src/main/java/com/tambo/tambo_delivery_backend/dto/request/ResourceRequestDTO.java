package com.tambo.tambo_delivery_backend.dto.request;

import lombok.Data;

@Data
public class ResourceRequestDTO {
    private String name;
    private String url;
    private Boolean isPrimary;
    private String type;
}
