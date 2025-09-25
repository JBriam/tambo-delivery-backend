package com.tambo.tambo_delivery_backend.services;

import com.paypal.orders.*;
import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;

import java.io.IOException;

@Service
public class PayPalService {

    private final PayPalHttpClient client;

    public PayPalService(
            @Value("${paypal.client.id}") String clientId,
            @Value("${paypal.client.secret}") String clientSecret,
            @Value("${paypal.mode}") String mode) {

        PayPalEnvironment environment = "live".equalsIgnoreCase(mode)
                ? new PayPalEnvironment.Live(clientId, clientSecret)
                : new PayPalEnvironment.Sandbox(clientId, clientSecret);

        this.client = new PayPalHttpClient(environment);
    }

    public Order captureOrder(String paypalOrderId) throws IOException {
        try {
            // 1. Primero verificar el estado actual de la orden
            OrdersGetRequest getRequest = new OrdersGetRequest(paypalOrderId);
            HttpResponse<Order> getResponse = client.execute(getRequest);
            Order order = getResponse.result();

            // 2. Si ya está completada, retornarla directamente
            if ("COMPLETED".equals(order.status())) {
                return order;
            }

            // 3. Si no está aprobada, no podemos capturarla
            if (!"APPROVED".equals(order.status())) {
                throw new RuntimeException("La orden no está en estado aprobado. Estado actual: " + order.status());
            }

            // 4. Intentar capturar solo si está aprobada
            OrdersCaptureRequest captureRequest = new OrdersCaptureRequest(paypalOrderId);
            captureRequest.requestBody(new OrderRequest());

            HttpResponse<Order> captureResponse = client.execute(captureRequest);

            if (captureResponse.statusCode() != 201) {
                throw new RuntimeException("Error al capturar pago. Código: " + captureResponse.statusCode());
            }

            return captureResponse.result();
        } catch (HttpException e) {
            // Manejo específico de errores de PayPal
            if (e.getMessage().contains("ORDER_ALREADY_CAPTURED")) {
                // Si ya estaba capturada, obtener los detalles nuevamente
                OrdersGetRequest getRequest = new OrdersGetRequest(paypalOrderId);
                HttpResponse<Order> getResponse = client.execute(getRequest);
                return getResponse.result();
            }
            throw new IOException("Error en PayPal: " + e.getMessage());
        }
    }
}