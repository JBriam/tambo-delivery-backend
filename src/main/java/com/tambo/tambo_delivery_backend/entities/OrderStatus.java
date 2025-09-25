package com.tambo.tambo_delivery_backend.entities;

public enum OrderStatus {
    PENDING, // pedido pendiente
    PAID, // pedido pagado
    IN_PROGRESS, // pedido en proceso
    SHIPPED, // pedido enviado
    DELIVERED, // pedido entregado
    CANCELLED, // pedido cancelado
    FAILED // pedido fallado
}
