package com.tambo.tambo_delivery_backend.dto.response;

import java.util.UUID;

import com.tambo.tambo_delivery_backend.entities.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDetail {

    private UUID id;
    private Product product;
    private Integer quantity;
    private Double itemPrice;
}
