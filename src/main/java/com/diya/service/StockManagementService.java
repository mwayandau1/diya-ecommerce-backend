package com.diya.service;

import com.diya.exception.InsufficientStockException;
import com.diya.model.CartItem;
import com.diya.model.Order;
import com.diya.model.OrderItem;
import com.diya.model.Product;
import com.diya.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockManagementService {

    private final ProductRepository productRepository;

    public void validateStock(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Not enough stock available for product: " + product.getName());
            }
        }
    }

    @Transactional
    public void updateStockForCheckout(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
    }

    @Transactional
    public void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
    }
}
