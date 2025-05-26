
package com.diya.mapper;

import com.diya.dto.request.CategoryRequest;
import com.diya.dto.response.CategoryResponse;
import com.diya.model.Category;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoryMapper {

    public CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .hasChildren(!category.getChildren().isEmpty())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public Category toCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        updateCategoryFromRequest(categoryRequest, category);
        return category;
    }

    public void updateCategoryFromRequest(CategoryRequest request, Category category) {
        category.setName(request.getName());
        
        if (request.getSlug() != null) {
            category.setSlug(request.getSlug());
        } else if (category.getSlug() == null) {
            String slug = request.getName()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "-")
                    .replaceAll("-+", "-")
                    .replaceAll("^-|-$", "");
            category.setSlug(slug);
        }
        
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        
        if (category.getCreatedAt() == null) {
            category.setCreatedAt(LocalDateTime.now());
        }
        category.setUpdatedAt(LocalDateTime.now());
    }
}
