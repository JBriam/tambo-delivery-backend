package com.tambo.tambo_delivery_backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tambo.tambo_delivery_backend.auth.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "latitude", nullable = true)
    private Double latitude;

    @Column(name = "longitude", nullable = true)
    private Double longitude;

    // monto total
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryMethod deliveryMethod;

    // monto total
    @Column(nullable = false)
    private Double totalAmount;

    // estado de pedido
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    // metodo de pago
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    // Número de seguimiento del envío
    @Column(nullable = true)
    private String shipmentTrackingNumber;

    // Fecha de entrega esperada
    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedDeliveryDate;

    // lista de artículos de pedido
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<OrderItem> orderItemList;

    // descuento
    private Double discount;

    // metodo depago
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Payment payment;

    // tipo de facturacion
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReceiptType receiptType;

    @Column(nullable = false)
    private String docType;

    @Column(nullable = false)
    private long docNumber;

    @Column(nullable = true)
    private String ruc;

    @Column(nullable = true)
    private String razonSocial;

}
