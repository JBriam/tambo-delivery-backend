package com.tambo.tambo_delivery_backend.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDiscountDTO {
    private UUID id;
    private String name;
    private BigDecimal price;
}
