package com.tambo.tambo_delivery_backend.controllers;

import com.tambo.tambo_delivery_backend.entities.OrderStatus;
import com.tambo.tambo_delivery_backend.services.OrderService;
import com.tambo.tambo_delivery_backend.services.PayPalService;
import com.paypal.orders.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PayPalService paypalService;
    private final OrderService orderService;

    public PaymentController(PayPalService paypalService, OrderService orderService) {
        this.paypalService = paypalService;
        this.orderService = orderService;
    }

    @PostMapping("/paypal/confirm-payment")
    public ResponseEntity<?> capturePayment(@RequestBody Map<String, String> request) {
        try {
            String orderIdStr = request.get("orderId");
            String paypalOrderId = request.get("paypalOrderId");

            UUID orderId = UUID.fromString(orderIdStr);

            // 1. Verificar si la orden ya fue procesada
            com.tambo.tambo_delivery_backend.entities.Order existingOrder = orderService.getOrderById(orderId);
            if (existingOrder.getOrderStatus() == OrderStatus.PAID) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "La orden ya estaba pagada"));
            }

            // 1. Capturar pago con PayPal
            Order paypalOrder = paypalService.captureOrder(paypalOrderId);

            // 3. Validar estado
            if (!"COMPLETED".equals(paypalOrder.status())) {
                throw new RuntimeException("El pago no se completó. Estado: " + paypalOrder.status());
            }

            // 2. Actualizar orden
            orderService.updateOrderStatus(
                    orderId,
                    OrderStatus.PAID,
                    paypalOrder.id());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "transactionId", paypalOrder.id()));

        } catch (IllegalArgumentException e) {
            log.error("ID de orden inválido", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "ID de orden inválido"));
        } catch (Exception e) {
            log.error("Error al procesar pago", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()));
        }
    }
}