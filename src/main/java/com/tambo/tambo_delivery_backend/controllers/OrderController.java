package com.tambo.tambo_delivery_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import com.tambo.tambo_delivery_backend.auth.dto.OrderResponse;
import com.tambo.tambo_delivery_backend.dto.OrderDetails;
import com.tambo.tambo_delivery_backend.dto.OrderRequest;
import com.tambo.tambo_delivery_backend.services.OrderService;
import com.tambo.tambo_delivery_backend.entities.Order;
import com.tambo.tambo_delivery_backend.services.BoletaPdfService;
import com.tambo.tambo_delivery_backend.services.FacturaPdfService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    private BoletaPdfService boletaPdfService;

    @Autowired
    private FacturaPdfService facturaPdfService;

    // Crear una nueva orden
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest, Principal principal) throws Exception {
        try {
            OrderResponse orderResponse = orderService.createOrder(orderRequest, principal);
            return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear la orden: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }

    // Obtener todas las órdenes del usuario autenticado
    @GetMapping
    public ResponseEntity<List<OrderDetails>> getUserOrders(Principal principal) {
        try {
            List<OrderDetails> orders = orderService.getUserOrders(principal);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener una orden específica por ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetails> getOrderById(@PathVariable UUID orderId, Principal principal) {
        try {
            OrderDetails order = orderService.getOrderDetailsById(orderId, principal);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint para descargar boleta PDF
    @GetMapping("/{orderId}/boleta")
    public ResponseEntity<byte[]> descargarBoleta(@PathVariable UUID orderId, Principal principal) {
        try {
            Order order = orderService.getOrderByIdAndUser(orderId, principal);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdf = boletaPdfService.generateBoletaPdf(order);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=boleta_tambo_" + orderId + ".pdf")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(pdf);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint para descargar factura PDF
    @GetMapping("/{orderId}/factura")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable UUID orderId, Principal principal) {
        try {
            Order order = orderService.getOrderByIdAndUser(orderId, principal);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdf = facturaPdfService.generateFacturaPdf(order);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura_tambo_" + orderId + ".pdf")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(pdf);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Cancelar una orden (solo si está en estado PENDIENTE o CONFIRMADA)
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable UUID orderId, Principal principal) {
        try {
            OrderDetails cancelledOrder = orderService.cancelOrder(orderId, principal);
            return ResponseEntity.ok(cancelledOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al cancelar la orden: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }
}
