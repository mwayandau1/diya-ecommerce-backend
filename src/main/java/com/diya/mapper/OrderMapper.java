
package com.diya.mapper;

import com.diya.dto.response.OrderItemResponse;
import com.diya.dto.response.OrderResponse;
import com.diya.model.Order;
import com.diya.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final UserMapper userMapper;
    private final AddressMapper addressMapper;
    private final PaymentMapper paymentMapper;

    public OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());
        
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .user(userMapper.toUserResponse(order.getUser()))
                .items(itemResponses)
                .shippingAddress(order.getShippingAddress() != null ? 
                        addressMapper.toAddressResponse(order.getShippingAddress()) : null)
                .billingAddress(order.getBillingAddress() != null ? 
                        addressMapper.toAddressResponse(order.getBillingAddress()) : null)
                .payment(order.getPayment() != null ? 
                        paymentMapper.toPaymentResponse(order.getPayment()) : null)
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .shippingCost(order.getShippingCost())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .trackingNumber(order.getTrackingNumber())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProductName())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .subtotal(orderItem.getSubtotal())
                .build();
    }
}
