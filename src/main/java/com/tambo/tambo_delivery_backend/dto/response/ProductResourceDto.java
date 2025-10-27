package com.tambo.tambo_delivery_backend.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResourceDto {

    private UUID id;
    private String name;
    private String url;
    private String type;
    private Boolean isPrimary;
}
