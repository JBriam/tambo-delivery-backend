package com.tambo.tambo_delivery_backend.dto.request;

import java.util.Date;
import java.util.List;

import com.tambo.tambo_delivery_backend.entities.DeliveryMethod;
import com.tambo.tambo_delivery_backend.entities.PaymentMethod;
import com.tambo.tambo_delivery_backend.entities.ReceiptType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    // private UUID userId;
    private Date orderDate; // fecha de creacion del pedido
    private Double latitude;
    private Double longitude;
    private List<OrderItemRequest> orderItemRequests; // productos del carrito
    private DeliveryMethod deliveryMethod; // metodo de entrega
    private Double totalAmount; // monto total a pagar
    private Double discount; // descuento
    private PaymentMethod paymentMethod; // Metodo de pago
    private Date expectedDeliveryDate; // Fecha de entrega esperada
    private ReceiptType receiptType; // tipo de facturacion
    private String docType; // tipo de documento de identidad
    private long docNumber; // numero del documento de identidad
    private String billingAddress; // ruc
    private String businessName; // Razon social

}
