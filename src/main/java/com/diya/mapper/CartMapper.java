
package com.diya.mapper;

import com.diya.dto.response.CartItemResponse;
import com.diya.dto.response.CartResponse;
import com.diya.model.Cart;
import com.diya.model.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final ProductMapper productMapper;

    public CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::toCartItemResponse)
                .collect(Collectors.toList());
        
        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalPrice(cart.getTotalPrice())
                .itemCount(cart.getItems().size())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .product(productMapper.toProductResponse(cartItem.getProduct()))
                .quantity(cartItem.getQuantity())
                .subtotal(cartItem.getSubtotal())
                .createdAt(cartItem.getCreatedAt())
                .updatedAt(cartItem.getUpdatedAt())
                .build();
    }
}
