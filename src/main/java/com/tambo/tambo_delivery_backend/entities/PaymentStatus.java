package com.tambo.tambo_delivery_backend.entities;

public enum PaymentStatus {
    PENDING, // pago pendiente
    COMPLETED, // pago completado
    FAILED, // pago fallido
    REFUNDED, // devolucion de dinero
}
