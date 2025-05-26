
package com.diya.controller;

import com.diya.dto.request.CartItemRequest;
import com.diya.dto.response.CartResponse;
import com.diya.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponse> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userDetails.getUsername(), request));
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponse> updateCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(userDetails.getUsername(), itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponse> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeFromCart(userDetails.getUsername(), itemId));
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponse> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.clearCart(userDetails.getUsername()));
    }
}
