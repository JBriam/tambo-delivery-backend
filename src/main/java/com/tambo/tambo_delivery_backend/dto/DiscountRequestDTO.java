package com.tambo.tambo_delivery_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class DiscountRequestDTO {
    private String name;
    private BigDecimal percentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private List<UUID> productIds;
}
