package com.tambo.tambo_delivery_backend.auth.dto.response;

import java.util.UUID;

import com.tambo.tambo_delivery_backend.entities.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private UUID orderId;
    // private Map<String, String> credentials;
    private PaymentMethod paymentMethod;

}
