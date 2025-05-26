
package com.diya.mapper;

import com.diya.dto.request.ProductRequest;
import com.diya.dto.response.ProductResponse;
import com.diya.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    public ProductResponse toProductResponse(Product product) {
        Map<String, String> attributesMap = product.getAttributes().stream()
                .collect(Collectors.toMap(Product.ProductAttribute::getKey, Product.ProductAttribute::getValue));
        
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .stock(product.getStock())
                .sku(product.getSku())
                .category(product.getCategory() != null ? categoryMapper.toCategoryResponse(product.getCategory()) : null)
                .images(product.getImages())
                .attributes(attributesMap)
                .active(product.isActive())
                .featured(product.isFeatured())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public Product toProduct(ProductRequest productRequest) {
        Product product = new Product();
        updateProductFromRequest(productRequest, product);
        return product;
    }

    public void updateProductFromRequest(ProductRequest request, Product product) {
        product.setName(request.getName());
        
        if (request.getSlug() != null) {
            product.setSlug(request.getSlug());
        } else if (product.getSlug() == null) {
            String slug = request.getName()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "-")
                    .replaceAll("-+", "-")
                    .replaceAll("^-|-$", "");
            product.setSlug(slug);
        }
        
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStock(request.getStock());
        product.setSku(request.getSku());
        product.setImages(request.getImages());
        product.setActive(request.isActive());
        product.setFeatured(request.isFeatured());
        
        // Convert map to set of product attributes
        if (request.getAttributes() != null) {
            HashSet<Product.ProductAttribute> attributes = new HashSet<>();
            request.getAttributes().forEach((key, value) -> {
                attributes.add(new Product.ProductAttribute(key, value));
            });
            product.setAttributes(attributes);
        }
        
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(LocalDateTime.now());
        }
        product.setUpdatedAt(LocalDateTime.now());
    }
}
