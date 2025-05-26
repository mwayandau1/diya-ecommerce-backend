
package com.diya.dto.request;

import com.diya.model.Order;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {
    
    @NotNull(message = "Order status is required")
    private Order.OrderStatus status;
    
    private String trackingNumber;
}
