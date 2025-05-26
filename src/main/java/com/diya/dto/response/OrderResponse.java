
package com.diya.dto.response;

import com.diya.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private UserResponse user;
    private List<OrderItemResponse> items = new ArrayList<>();
    private AddressResponse shippingAddress;
    private AddressResponse billingAddress;
    private PaymentResponse payment;
    private Order.OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String trackingNumber;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
