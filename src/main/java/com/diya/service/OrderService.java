package com.diya.service;

import com.diya.dto.request.CheckoutRequest;
import com.diya.dto.request.OrderStatusUpdateRequest;
import com.diya.dto.response.OrderResponse;
import com.diya.dto.response.PagedResponse;
import com.diya.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CheckoutService checkoutService;
    private final OrderManagementService orderManagementService;

    public PagedResponse<OrderResponse> getAllOrders(Pageable pageable) {
        return orderManagementService.getAllOrders(pageable);
    }

    public OrderResponse getOrderById(Long id) {
        return orderManagementService.getOrderById(id);
    }

    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        return orderManagementService.getOrderByOrderNumber(orderNumber);
    }

    public PagedResponse<OrderResponse> getOrdersByUser(String username, Pageable pageable) {
        return orderManagementService.getOrdersByUser(username, pageable);
    }

    public PagedResponse<OrderResponse> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderManagementService.getOrdersByStatus(status, pageable);
    }

    public OrderResponse checkout(String username, CheckoutRequest checkoutRequest) {
        return checkoutService.checkout(username, checkoutRequest);
    }

    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        return orderManagementService.updateOrderStatus(id, request);
    }

}
