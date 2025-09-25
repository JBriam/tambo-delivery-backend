package com.tambo.tambo_delivery_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountRequestDTO {
    String name;
    BigDecimal percentage;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Boolean isActive;
    List<UUID> productIds;
}
