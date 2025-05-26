package com.diya.controller;

import com.diya.dto.request.PromotionRequest;
import com.diya.dto.response.MessageResponse;
import com.diya.dto.response.PagedResponse;
import com.diya.dto.response.PromotionResponse;
import com.diya.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<PagedResponse<PromotionResponse>> getAllPromotions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        PagedResponse<PromotionResponse> promotions = promotionService.getAllPromotions(pageable);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/active")
    public ResponseEntity<PagedResponse<PromotionResponse>> getActivePromotions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PagedResponse<PromotionResponse> promotions = promotionService.getActivePromotions(pageable);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponse> getPromotionById(@PathVariable Long id) {
        PromotionResponse promotion = promotionService.getPromotionById(id);
        return ResponseEntity.ok(promotion);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<PromotionResponse> getPromotionByCode(@PathVariable String code) {
        PromotionResponse promotion = promotionService.getPromotionByCode(code);
        return ResponseEntity.ok(promotion);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromotionResponse> createPromotion(@Valid @RequestBody PromotionRequest promotionRequest) {
        PromotionResponse promotion = promotionService.createPromotion(promotionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(promotion);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromotionResponse> updatePromotion(
            @PathVariable Long id,
            @Valid @RequestBody PromotionRequest promotionRequest) {
        PromotionResponse updatedPromotion = promotionService.updatePromotion(id, promotionRequest);
        return ResponseEntity.ok(updatedPromotion);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok(new MessageResponse("Promotion deleted successfully"));
    }
}
