package com.tambo.tambo_delivery_backend.entities;

public enum DeliveryStatus {
    PENDING, // esperando asignación
    ASSIGNED, // repartidor asignado
    IN_TRANSIT, // en camino
    DELIVERED, // entregado
    CANCELLED // cancelado
}
