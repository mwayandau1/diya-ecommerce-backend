
package com.diya.service;

import com.diya.dto.request.CheckoutRequest;
import com.diya.dto.request.OrderStatusUpdateRequest;
import com.diya.dto.response.OrderResponse;
import com.diya.dto.response.PagedResponse;
import com.diya.exception.InsufficientStockException;
import com.diya.exception.ResourceNotFoundException;
import com.diya.mapper.OrderMapper;
import com.diya.model.*;
import com.diya.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final OrderMapper orderMapper;
    private final CartItemRepository cartItemRepository;

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
    public OrderResponse checkout(String username, CheckoutRequest checkoutRequest) {
        User user = findUserByEmail(username);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + username));
        
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart");
        }
        
        // Validate shipping address
        Address shippingAddress;
        if (checkoutRequest.getShippingAddressId() != null) {
            shippingAddress = addressRepository.findById(checkoutRequest.getShippingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));
            
            if (!shippingAddress.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Shipping address does not belong to the user");
            }
        } else {
            throw new IllegalArgumentException("Shipping address is required");
        }
        
        // Validate billing address if provided, otherwise use shipping address
        Address billingAddress;
        if (checkoutRequest.getBillingAddressId() != null) {
            billingAddress = addressRepository.findById(checkoutRequest.getBillingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Billing address not found"));
                    
            if (!billingAddress.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Billing address does not belong to the user");
            }
        } else {
            billingAddress = shippingAddress;
        }
        
        // Check stock and validate products
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Not enough stock available for product: " + product.getName());
            }
        }

        // Calculate totals
        BigDecimal subtotal = cart.getTotalPrice();
        BigDecimal shippingCost = checkoutRequest.getShippingMethod().equals("express") ? 
                BigDecimal.valueOf(15) : BigDecimal.valueOf(5);
        BigDecimal taxRate = BigDecimal.valueOf(0.10); // 10% tax rate
        BigDecimal taxAmount = subtotal.multiply(taxRate);
        BigDecimal totalAmount = subtotal.add(shippingCost).add(taxAmount);

        // Create the order
        Order order = Order.builder()
                .user(user)
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .notes(checkoutRequest.getNotes())
                .build();
        
        orderRepository.save(order);
        
        // Add order items
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            
            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .price(product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(cartItem.getSubtotal())
                    .build();
            
            order.getItems().add(orderItem);
            orderItemRepository.save(orderItem);
            
            // Update product stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        // Create payment record
        Payment payment = Payment.builder()
                .order(order)
                .transactionId("TXN-" + System.currentTimeMillis())
                .paymentMethod(Payment.PaymentMethod.valueOf(checkoutRequest.getPaymentMethod()))
                .status(Payment.PaymentStatus.PENDING)
                .amount(totalAmount)
                .build();
        
        paymentRepository.save(payment);
        order.setPayment(payment);
        
        // Clear the cart
        cart.getItems().clear();
        cartItemRepository.deleteAllByCartId(cart.getId());
        
        return orderMapper.toOrderResponse(order);
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
            restoreProductStock(order);
        }
        
        // Update payment status if needed
        Payment payment = order.getPayment();
        if (payment != null) {
            if (order.getStatus() == Order.OrderStatus.DELIVERED) {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
            } else if (order.getStatus() == Order.OrderStatus.CANCELLED) {
                payment.setStatus(Payment.PaymentStatus.REFUNDED);
            }
            paymentRepository.save(payment);
        }
        
        return orderMapper.toOrderResponse(updatedOrder);
    }
    
    private void restoreProductStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
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
