

package com.diya.service;


import com.diya.dto.request.CartItemRequest;
import com.diya.dto.response.CartResponse;
import com.diya.exception.InsufficientStockException;
import com.diya.exception.ResourceNotFoundException;
import com.diya.mapper.CartMapper;
import com.diya.model.Cart;
import com.diya.model.CartItem;
import com.diya.model.Product;
import com.diya.model.User;
import com.diya.repository.CartItemRepository;
import com.diya.repository.CartRepository;
import com.diya.repository.ProductRepository;
import com.diya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartResponse getCart(String username) {
        User user = findUserByEmail(username);
        Cart cart = findOrCreateCart(user);
        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(String username, CartItemRequest request) {
        User user = findUserByEmail(username);
        Cart cart = findOrCreateCart(user);

        Product product = findProductById(request.getProductId());

        // Check if product has enough stock
        if (product.getStock() < request.getQuantity()) {
            throw new InsufficientStockException("Not enough stock available for product: " + product.getName());
        }

        // Check if the product is already in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update existing item quantity
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            if (product.getStock() < request.getQuantity()) {
                throw new InsufficientStockException("Not enough stock available for product: " + product.getName());
            }

            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);

            // Reduce product stock
            product.setStock(product.getStock() - request.getQuantity());
            productRepository.save(product);
        } else {
            // Add new cart item
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();

            cart.getItems().add(cartItem);
            cartItemRepository.save(cartItem);

            // Reduce product stock
            product.setStock(product.getStock() - request.getQuantity());
            productRepository.save(product);
        }

        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(String username, Long cartItemId, CartItemRequest request) {
        User user = findUserByEmail(username);
        Cart cart = findOrCreateCart(user);

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        Product product = cartItem.getProduct();
        int quantityDifference = request.getQuantity() - cartItem.getQuantity();

        if (request.getQuantity() <= 0) {
            // Return items to stock when removing from cart
            product.setStock(product.getStock() + cartItem.getQuantity());
            productRepository.save(product);

            cart.getItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            // Check if increasing quantity
            if (quantityDifference > 0) {
                // Ensure enough stock for the increase
                if (product.getStock() < quantityDifference) {
                    throw new InsufficientStockException("Not enough stock available for product: " + product.getName());
                }

                // Decrease stock by the difference
                product.setStock(product.getStock() - quantityDifference);
            } else if (quantityDifference < 0) {
                // Return items to stock
                product.setStock(product.getStock() - quantityDifference); // negative difference, so we subtract
            }

            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
            productRepository.save(product);
        }

        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public CartResponse removeFromCart(String username, Long cartItemId) {
        User user = findUserByEmail(username);
        Cart cart = findOrCreateCart(user);

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Return items to stock
        Product product = cartItem.getProduct();
        product.setStock(product.getStock() + cartItem.getQuantity());
        productRepository.save(product);

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public CartResponse clearCart(String username) {
        User user = findUserByEmail(username);
        Cart cart = findOrCreateCart(user);

        // Return all items to stock
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        cartItemRepository.deleteAllByCartId(cart.getId());
        cart.getItems().clear();

        return cartMapper.toCartResponse(cart);
    }

    private Cart findOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
