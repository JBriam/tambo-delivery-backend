package com.tambo.tambo_delivery_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DiscountDTO {
    private UUID id;
    private String name;
    private BigDecimal percentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private List<ProductDiscountDTO> products;
}
