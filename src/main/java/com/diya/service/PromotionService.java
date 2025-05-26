package com.diya.service;

import com.diya.dto.request.PromotionRequest;
import com.diya.dto.response.PagedResponse;
import com.diya.dto.response.PromotionResponse;
import com.diya.exception.ResourceNotFoundException;
import com.diya.mapper.PromotionMapper;
import com.diya.model.Category;
import com.diya.model.Product;
import com.diya.model.Promotion;
import com.diya.repository.CategoryRepository;
import com.diya.repository.ProductRepository;
import com.diya.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PromotionMapper promotionMapper;

    public PagedResponse<PromotionResponse> getAllPromotions(Pageable pageable) {
        Page<Promotion> promotions = promotionRepository.findAll(pageable);
        return createPagedResponse(promotions);
    }

    public PagedResponse<PromotionResponse> getActivePromotions(Pageable pageable) {
        Page<Promotion> promotions = promotionRepository.findByActiveTrue(pageable);
        return createPagedResponse(promotions);
    }

    public PromotionResponse getPromotionById(Long id) {
        Promotion promotion = findPromotionById(id);
        return promotionMapper.toPromotionResponse(promotion);
    }

    public PromotionResponse getPromotionByCode(String code) {
        Promotion promotion = promotionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with code: " + code));
        return promotionMapper.toPromotionResponse(promotion);
    }

    @Transactional
    public PromotionResponse createPromotion(PromotionRequest request) {
        Promotion promotion = promotionMapper.toPromotion(request);

        // Set applicable categories
        if (request.getApplicableCategoryIds() != null && !request.getApplicableCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : request.getApplicableCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
                categories.add(category);
            }
            promotion.setApplicableCategories(categories);
        }

        // Set applicable products
        if (request.getApplicableProductIds() != null && !request.getApplicableProductIds().isEmpty()) {
            Set<Product> products = new HashSet<>();
            for (Long productId : request.getApplicableProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
                products.add(product);
            }
            promotion.setApplicableProducts(products);
        }

        Promotion savedPromotion = promotionRepository.save(promotion);
        return promotionMapper.toPromotionResponse(savedPromotion);
    }

    @Transactional
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion existingPromotion = findPromotionById(id);
        promotionMapper.updatePromotionFromRequest(request, existingPromotion);

        // Update applicable categories
        existingPromotion.getApplicableCategories().clear();
        if (request.getApplicableCategoryIds() != null && !request.getApplicableCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : request.getApplicableCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
                categories.add(category);
            }
            existingPromotion.setApplicableCategories(categories);
        }

        // Update applicable products
        existingPromotion.getApplicableProducts().clear();
        if (request.getApplicableProductIds() != null && !request.getApplicableProductIds().isEmpty()) {
            Set<Product> products = new HashSet<>();
            for (Long productId : request.getApplicableProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
                products.add(product);
            }
            existingPromotion.setApplicableProducts(products);
        }

        Promotion updatedPromotion = promotionRepository.save(existingPromotion);
        return promotionMapper.toPromotionResponse(updatedPromotion);
    }

    @Transactional
    public void deletePromotion(Long id) {
        Promotion promotion = findPromotionById(id);
        promotionRepository.delete(promotion);
    }

    public BigDecimal calculateDiscount(String promotionCode, BigDecimal orderAmount, List<Product> orderProducts) {
        Promotion promotion = promotionRepository.findValidPromotionByCode(promotionCode, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired promotion code: " + promotionCode));

        // Check minimum order amount
        if (promotion.getMinimumOrderAmount() != null &&
                orderAmount.compareTo(promotion.getMinimumOrderAmount()) < 0) {
            throw new IllegalArgumentException("Order amount does not meet minimum requirement for this promotion");
        }

        // Check if promotion applies to any products in the order
        if (!promotion.getApplicableProducts().isEmpty() || !promotion.getApplicableCategories().isEmpty()) {
            boolean applicable = orderProducts.stream().anyMatch(product ->
                    promotion.getApplicableProducts().contains(product) ||
                            promotion.getApplicableCategories().contains(product.getCategory())
            );

            if (!applicable) {
                throw new IllegalArgumentException("This promotion is not applicable to the products in your order");
            }
        }

        BigDecimal discount = BigDecimal.ZERO;

        switch (promotion.getType()) {
            case PERCENTAGE:
                discount = orderAmount.multiply(promotion.getValue()).divide(BigDecimal.valueOf(100));
                break;
            case FIXED_AMOUNT:
                discount = promotion.getValue();
                break;
            case FREE_SHIPPING:
                // This would be handled in the shipping calculation logic
                discount = BigDecimal.ZERO;
                break;
            case BUY_ONE_GET_ONE:
                // This would require more complex logic based on product quantities
                discount = BigDecimal.ZERO;
                break;
        }

        // Apply maximum discount limit if set
        if (promotion.getMaximumDiscountAmount() != null &&
                discount.compareTo(promotion.getMaximumDiscountAmount()) > 0) {
            discount = promotion.getMaximumDiscountAmount();
        }

        return discount;
    }

    @Transactional
    public void incrementUsageCount(String promotionCode) {
        Promotion promotion = promotionRepository.findByCode(promotionCode)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with code: " + promotionCode));

        promotion.setUsageCount(promotion.getUsageCount() + 1);
        promotionRepository.save(promotion);
    }

    private Promotion findPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
    }

    private PagedResponse<PromotionResponse> createPagedResponse(Page<Promotion> promotions) {
        List<PromotionResponse> responses = promotions.getContent()
                .stream()
                .map(promotionMapper::toPromotionResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                promotions.getNumber(),
                promotions.getSize(),
                promotions.getTotalElements(),
                promotions.getTotalPages(),
                promotions.isLast()
        );
    }
}
