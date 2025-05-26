package com.diya.service;

import com.diya.dto.request.CheckoutRequest;
import com.diya.dto.response.OrderResponse;
import com.diya.exception.ResourceNotFoundException;
import com.diya.mapper.OrderMapper;
import com.diya.model.*;
import com.diya.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CartItemRepository cartItemRepository;
    private final StockManagementService stockManagementService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse checkout(String username, CheckoutRequest checkoutRequest) {
        User user = findUserByEmail(username);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + username));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart");
        }

        // Validate addresses
        Address shippingAddress = validateShippingAddress(checkoutRequest, user);
        Address billingAddress = validateBillingAddress(checkoutRequest, user, shippingAddress);

        // Check stock and validate products
        stockManagementService.validateStock(cart.getItems());

        // Calculate totals
        BigDecimal subtotal = cart.getTotalPrice();
        BigDecimal shippingCost = calculateShippingCost(checkoutRequest.getShippingMethod());
        BigDecimal taxRate = BigDecimal.valueOf(0.10); // 10% tax rate
        BigDecimal taxAmount = subtotal.multiply(taxRate);
        BigDecimal totalAmount = subtotal.add(shippingCost).add(taxAmount);

        // Create the order
        Order order = createOrder(user, shippingAddress, billingAddress, subtotal, shippingCost, taxAmount, totalAmount, checkoutRequest.getNotes());

        // Add order items
        createOrderItems(cart, order);

        // Update product stock
        stockManagementService.updateStockForCheckout(cart.getItems());

        // Create payment record
        createPayment(order, totalAmount, checkoutRequest.getPaymentMethod());

        // Clear the cart
        clearCart(cart);

        return orderMapper.toOrderResponse(order);
    }

    private Address validateShippingAddress(CheckoutRequest checkoutRequest, User user) {
        if (checkoutRequest.getShippingAddressId() == null) {
            throw new IllegalArgumentException("Shipping address is required");
        }

        Address shippingAddress = addressRepository.findById(checkoutRequest.getShippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));

        if (!shippingAddress.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Shipping address does not belong to the user");
        }

        return shippingAddress;
    }

    private Address validateBillingAddress(CheckoutRequest checkoutRequest, User user, Address shippingAddress) {
        if (checkoutRequest.getBillingAddressId() != null) {
            Address billingAddress = addressRepository.findById(checkoutRequest.getBillingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Billing address not found"));

            if (!billingAddress.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Billing address does not belong to the user");
            }

            return billingAddress;
        }

        return shippingAddress;
    }

    private BigDecimal calculateShippingCost(String shippingMethod) {
        return "express".equals(shippingMethod) ? BigDecimal.valueOf(15) : BigDecimal.valueOf(5);
    }

    private Order createOrder(User user, Address shippingAddress, Address billingAddress,
                              BigDecimal subtotal, BigDecimal shippingCost, BigDecimal taxAmount,
                              BigDecimal totalAmount, String notes) {
        Order order = Order.builder()
                .user(user)
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .notes(notes)
                .build();

        return orderRepository.save(order);
    }

    private void createOrderItems(Cart cart, Order order) {
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

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
        }
    }

    private void createPayment(Order order, BigDecimal totalAmount, String paymentMethod) {
        Payment payment = Payment.builder()
                .order(order)
                .transactionId("TXN-" + System.currentTimeMillis())
                .paymentMethod(Payment.PaymentMethod.valueOf(paymentMethod))
                .status(Payment.PaymentStatus.PENDING)
                .amount(totalAmount)
                .build();

        paymentRepository.save(payment);
        order.setPayment(payment);
    }

    private void clearCart(Cart cart) {
        cart.getItems().clear();
        cartItemRepository.deleteAllByCartId(cart.getId());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}