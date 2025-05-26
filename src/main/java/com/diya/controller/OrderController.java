
package com.diya.controller;


import com.diya.dto.request.CheckoutRequest;
import com.diya.dto.request.OrderStatusUpdateRequest;
import com.diya.dto.response.OrderResponse;
import com.diya.dto.response.PagedResponse;
import com.diya.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CheckoutRequest checkoutRequest) {
        return ResponseEntity.ok(orderService.checkout(userDetails.getUsername(), checkoutRequest));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PagedResponse<OrderResponse>> getUserOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(orderService.getOrdersByUser(userDetails.getUsername(), pageable));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<OrderResponse> getOrderById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @PutMapping("/admin/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest statusUpdateRequest) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, statusUpdateRequest));
    }
}
