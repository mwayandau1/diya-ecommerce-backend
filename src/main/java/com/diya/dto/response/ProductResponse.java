
package com.diya.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stock;
    private String sku;
    private CategoryResponse category;
    private List<String> images = new ArrayList<>();
    private Map<String, String> attributes = new HashMap<>();
    private boolean active;
    private boolean featured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
