
package com.diya.service;

import com.diya.dto.request.CategoryRequest;
import com.diya.dto.response.CategoryResponse;
import com.diya.exception.DuplicateResourceException;
import com.diya.exception.ResourceNotFoundException;
import com.diya.mapper.CategoryMapper;
import com.diya.model.Category;
import com.diya.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = findCategoryById(id);
        return categoryMapper.toCategoryResponse(category);
    }

    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return categoryMapper.toCategoryResponse(category);
    }

    public List<CategoryResponse> getSubcategories(Long parentId) {
        if (!categoryRepository.existsById(parentId)) {
            throw new ResourceNotFoundException("Parent category not found with id: " + parentId);
        }
        
        return categoryRepository.findSubcategories(parentId).stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new DuplicateResourceException("Category already exists with name: " + categoryRequest.getName());
        }
        
        Category category = categoryMapper.toCategory(categoryRequest);
        
        if (categoryRequest.getParentId() != null) {
            Category parentCategory = findCategoryById(categoryRequest.getParentId());
            category.setParent(parentCategory);
        }
        
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = findCategoryById(id);
        
        if (!category.getName().equals(categoryRequest.getName()) && 
            categoryRepository.existsByName(categoryRequest.getName())) {
            throw new DuplicateResourceException("Category already exists with name: " + categoryRequest.getName());
        }
        
        categoryMapper.updateCategoryFromRequest(categoryRequest, category);
        
        if (categoryRequest.getParentId() != null) {
            if (id.equals(categoryRequest.getParentId())) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }
            
            Category parentCategory = findCategoryById(categoryRequest.getParentId());
            category.setParent(parentCategory);
        } else {
            category.setParent(null);
        }
        
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategoryById(id);
        categoryRepository.delete(category);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }
}
