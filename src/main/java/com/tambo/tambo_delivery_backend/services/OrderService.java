package com.tambo.tambo_delivery_backend.services;

import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tambo.tambo_delivery_backend.auth.dto.response.OrderResponse;
import com.tambo.tambo_delivery_backend.auth.entities.User;
import com.tambo.tambo_delivery_backend.auth.services.EmailService;
import com.tambo.tambo_delivery_backend.dto.request.OrderRequest;
import com.tambo.tambo_delivery_backend.dto.response.OrderDetails;
import com.tambo.tambo_delivery_backend.dto.response.OrderItemDetail;
import com.tambo.tambo_delivery_backend.entities.Order;
import com.tambo.tambo_delivery_backend.entities.OrderItem;
import com.tambo.tambo_delivery_backend.entities.OrderStatus;
import com.tambo.tambo_delivery_backend.entities.Payment;
import com.tambo.tambo_delivery_backend.entities.PaymentStatus;
import com.tambo.tambo_delivery_backend.entities.Product;
import com.tambo.tambo_delivery_backend.entities.ReceiptType;
import com.tambo.tambo_delivery_backend.mapper.ProductMapper;
import com.tambo.tambo_delivery_backend.repositories.OrderRepository;
import com.tambo.tambo_delivery_backend.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BoletaPdfService boletaPdfService;

    @Autowired
    private FacturaPdfService facturaPdfService;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, Principal principal) throws Exception {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

        // Address address = user.getAddressList().stream()
        // .filter(address1 ->
        // orderRequest.getAddressId().equals(address1.getId())).findFirst().orElse(null);
        // .orElseThrow(BadRequestException::new);

        Order order = Order.builder()
                .orderDate(orderRequest.getOrderDate() != null ? orderRequest.getOrderDate() : new Date())
                .user(user)
                // .address(address != null ? address : null)
                .latitude(orderRequest.getLatitude())
                .longitude(orderRequest.getLongitude())
                .deliveryMethod(orderRequest.getDeliveryMethod())
                .totalAmount(orderRequest.getTotalAmount())
                .orderStatus(OrderStatus.PENDING)
                .paymentMethod(orderRequest.getPaymentMethod())
                .expectedDeliveryDate(orderRequest.getExpectedDeliveryDate())
                .discount(orderRequest.getDiscount())
                .receiptType(orderRequest.getReceiptType())
                .docType(orderRequest.getDocType())
                .docNumber(orderRequest.getDocNumber())
                .ruc(orderRequest.getBillingAddress())
                .razonSocial(orderRequest.getBusinessName())
                .build();

        List<OrderItem> orderItems = orderRequest.getOrderItemRequests().stream().map(orderItemRequest -> {
            try {
                Product product = productService.getProductEntityById(orderItemRequest.getProductId());
                
                // Si no viene itemPrice, usar el precio del producto
                Double itemPrice = orderItemRequest.getItemPrice() != null 
                    ? orderItemRequest.getItemPrice() 
                    : product.getPrice().doubleValue();
                
                OrderItem orderItem = OrderItem.builder()
                        .product(product)
                        .quantity(orderItemRequest.getQuantity())
                        .order(order)
                        .itemPrice(itemPrice)
                        .build();
                return orderItem;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();

        order.setOrderItemList(orderItems);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentDate(new Date());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(order.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        OrderResponse orderResponse = OrderResponse.builder()
                .paymentMethod(orderRequest.getPaymentMethod())
                .orderId(savedOrder.getId())
                .build();

        return orderResponse;

    }

    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status, String transactionId) {
        try {
            // 1. Validar parámetros
            if (orderId == null) {
                throw new IllegalArgumentException("ID de orden no puede ser nulo");
            }

            if (status == null) {
                throw new IllegalArgumentException("Estado no puede ser nulo");
            }

            // 2. Obtener la orden
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new BadRequestException("Orden no encontrada"));

            // 3. Actualizar stock si el status es PAID
            if (status == OrderStatus.PAID) {
                List<OrderItem> orderItems = order.getOrderItemList();

                for (OrderItem item : orderItems) {
                    // Obtener el producto asociado al item
                    Product product = item.getProduct();

                    // Validar que haya suficiente stock
                    if (product.getStock() < item.getQuantity()) {
                        throw new IllegalStateException(
                                "Stock insuficiente para el producto: " + product.getName() +
                                        ". Stock actual: " + product.getStock() +
                                        ", cantidad solicitada: " + item.getQuantity());
                    }

                    // Actualizar el stock restando la cantidad comprada
                    int newStock = product.getStock() - item.getQuantity();
                    product.setStock(newStock);

                    if (newStock == 0) {
                        product.setActive(false);
                    }

                    // Guardar el producto actualizado
                    productRepository.save(product);
                }

            }

            // 4. Validar transición de estado
            if (!isValidStatusTransition(order.getOrderStatus(), status)) {
                throw new IllegalStateException("Transición de estado no permitida");
            }

            // 5. Actualizar estado
            order.setOrderStatus(status);

            // 6. Actualizar pago si existe
            if (order.getPayment() != null) {
                updatePaymentStatus(order.getPayment(), status, transactionId);
            }

            orderRepository.save(order);
            // Si se pagó, enviar boleta
            if (status == OrderStatus.PAID) {

                try {
                    if (order.getReceiptType() == ReceiptType.BOLETA) {
                        byte[] pdf = boletaPdfService.generateBoletaPdf(order);
                        emailService.sendBoletaEmail(order, pdf);
                    } else if (order.getReceiptType() == ReceiptType.FACTURA) {
                        byte[] pdf = facturaPdfService.generateFacturaPdf(order);
                        emailService.sendFacturaEmail(order, pdf);
                    }
                } catch (Exception e) {
                    log.error("Error al enviar el comprobante de pago: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error al actualizar el estado de la orden: " + e.getMessage());
        }

    }

    private void updatePaymentStatus(Payment payment, OrderStatus status, String transactionId) {
        PaymentStatus paymentStatus = switch (status) {
            case PAID -> PaymentStatus.COMPLETED;
            case CANCELLED -> PaymentStatus.REFUNDED;
            default -> PaymentStatus.FAILED;
        };

        payment.setPaymentStatus(paymentStatus);

        if (transactionId != null) {
            payment.setTransactionId(transactionId);
        }

        payment.setPaymentDate(new Date());
    }

    private boolean isValidStatusTransition(OrderStatus current, OrderStatus newStatus) {
        if (newStatus == OrderStatus.CANCELLED) {
            return current == OrderStatus.PENDING || current == OrderStatus.PAID;
        }
        return true;
    }

    // Método adicional para obtener la orden
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public OrderDetails getOrderDtoById(UUID id) {
        try {

            Order order = orderRepository.findById(id).orElse(null);

            OrderDetails orderDto = convertToOrderDetails(order);

            return orderDto;

        } catch (Exception e) {
            log.error("Error al obtener el orden: {}", e);
            throw new RuntimeException("Error al recuperar la orden", e);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderDetails> getOrdersByUser(String email) {
        try {
            User user = (User) userDetailsService.loadUserByUsername(email);
            if (user == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            return orderRepository.findByUserWithItemsAndAddress(user).stream()
                    .map(this::convertToOrderDetails)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error al obtener órdenes del usuario: {}", email, e);
            throw new RuntimeException("Error al recuperar las órdenes", e);
        }
    }

    private OrderDetails convertToOrderDetails(Order order) {
        return OrderDetails.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .shipmentNumber(order.getShipmentTrackingNumber())
                .latitude(order.getLatitude())
                .longitude(order.getLongitude())
                // .address(convertToAddressDto(order.getAddress()))
                .totalAmount(order.getTotalAmount())
                .orderItemList(convertToItemDetails(order.getOrderItemList()))
                .expectedDeliveryDate(order.getExpectedDeliveryDate())
                .build();
    }

    private List<OrderItemDetail> convertToItemDetails(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                .map(this::convertToItemDetail)
                .collect(Collectors.toList());
    }

    private OrderItemDetail convertToItemDetail(OrderItem item) {
        return OrderItemDetail.builder()
                .id(item.getId())
                .product(item.getProduct())
                .quantity(item.getQuantity())
                .itemPrice(item.getItemPrice())
                .build();
    }

    public List<Order> getAllOrdersByDateDesc() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public boolean canOrderBeCancelled(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            return false;
        }

        // Verifica si el estado de la order es PENDING O PAID
        return order.getOrderStatus() == OrderStatus.PENDING || order.getOrderStatus() == OrderStatus.PAID;
    }

}
