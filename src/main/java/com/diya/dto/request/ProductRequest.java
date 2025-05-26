
package com.diya.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String slug;
    
    private String description;
    
    @NotNull(message = "Product price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private BigDecimal price;
    
    private BigDecimal discountPrice;
    
    @NotNull(message = "Product stock is required")
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Integer stock;
    
    @NotBlank(message = "Product SKU is required")
    private String sku;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    private List<String> images = new ArrayList<>();
    
    private Map<String, String> attributes = new HashMap<>();
    
    private boolean active = true;
    
    private boolean featured = false;
}
