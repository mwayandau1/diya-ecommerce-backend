package com.diya.service;

import com.diya.dto.request.OrderStatusUpdateRequest;
import com.diya.dto.response.OrderResponse;
import com.diya.dto.response.PagedResponse;
import com.diya.exception.ResourceNotFoundException;
import com.diya.mapper.OrderMapper;
import com.diya.model.Order;
import com.diya.model.Payment;
import com.diya.model.User;
import com.diya.repository.OrderRepository;
import com.diya.repository.PaymentRepository;
import com.diya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderManagementService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final StockManagementService stockManagementService;
    private final OrderMapper orderMapper;

    public PagedResponse<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return createPagedResponse(orders);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = findOrderById(id);
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
        return orderMapper.toOrderResponse(order);
    }

    public PagedResponse<OrderResponse> getOrdersByUser(String username, Pageable pageable) {
        User user = findUserByEmail(username);
        Page<Order> orders = orderRepository.findByUser(user, pageable);
        return createPagedResponse(orders);
    }

    public PagedResponse<OrderResponse> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByStatus(status, pageable);
        return createPagedResponse(orders);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = findOrderById(id);
        order.setStatus(request.getStatus());

        if (order.getStatus() == Order.OrderStatus.SHIPPED && request.getTrackingNumber() != null) {
            order.setTrackingNumber(request.getTrackingNumber());
        }

        Order updatedOrder = orderRepository.save(order);

        // If the order is canceled, restore product stock
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            stockManagementService.restoreStock(order);
        }

        // Update payment status if needed
        updatePaymentStatus(order);

        return orderMapper.toOrderResponse(updatedOrder);
    }

    private void updatePaymentStatus(Order order) {
        Payment payment = order.getPayment();
        if (payment != null) {
            if (order.getStatus() == Order.OrderStatus.DELIVERED) {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
            } else if (order.getStatus() == Order.OrderStatus.CANCELLED) {
                payment.setStatus(Payment.PaymentStatus.REFUNDED);
            }
            paymentRepository.save(payment);
        }
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private PagedResponse<OrderResponse> createPagedResponse(Page<Order> orders) {
        List<OrderResponse> orderResponses = orders.getContent()
                .stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                orderResponses,
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages(),
                orders.isLast()
        );
    }
}