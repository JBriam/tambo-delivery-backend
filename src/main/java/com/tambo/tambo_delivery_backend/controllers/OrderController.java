package com.tambo.tambo_delivery_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import com.tambo.tambo_delivery_backend.auth.dto.response.OrderResponse;
import com.tambo.tambo_delivery_backend.dto.request.OrderRequest;
import com.tambo.tambo_delivery_backend.dto.response.OrderDetails;
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

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest, Principal principal) throws Exception {
        OrderResponse orderResponse = orderService.createOrder(orderRequest, principal);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

    // Endpoint para descargar boleta PDF
    @GetMapping("/{orderId}/boleta")
    public ResponseEntity<byte[]> descargarBoleta(@PathVariable UUID orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdf = boletaPdfService.generateBoletaPdf(order);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=boleta_tambo.pdf")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Endpoint para descargar factura PDF
    @GetMapping("/{orderId}/factura")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable UUID orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdf = facturaPdfService.generateFacturaPdf(order);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura_tambo.pdf")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
