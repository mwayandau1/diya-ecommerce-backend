
package com.diya.service;
import com.diya.dto.request.ProductRequest;
import com.diya.dto.response.PagedResponse;
import com.diya.dto.response.ProductResponse;
import com.diya.exception.ResourceNotFoundException;
import com.diya.mapper.ProductMapper;
import com.diya.model.Category;
import com.diya.model.Product;
import com.diya.repository.CategoryRepository;
import com.diya.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public PagedResponse<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return createPagedResponse(products);
    }

    public ProductResponse getProductById(Long id) {
        Product product = findProductById(id);
        return productMapper.toProductResponse(product);
    }

    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));
        return productMapper.toProductResponse(product);
    }

    public PagedResponse<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        
        Page<Product> products = productRepository.findByCategory(category, pageable);
        return createPagedResponse(products);
    }

    public PagedResponse<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.search(keyword, pageable);
        return createPagedResponse(products);
    }

    public List<ProductResponse> getFeaturedProducts(int limit) {
        return productRepository.findAllFeaturedProducts(Pageable.ofSize(limit))
                .stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productRequest.getCategoryId()));
        
        Product product = productMapper.toProduct(productRequest);
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        
        return productMapper.toProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product existingProduct = findProductById(id);
        
        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productRequest.getCategoryId()));
            existingProduct.setCategory(category);
        }
        
        productMapper.updateProductFromRequest(productRequest, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        
        return productMapper.toProductResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse updateProductStock(Long id, int quantity) {
        Product product = findProductById(id);
        product.setStock(product.getStock() + quantity);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductResponse(updatedProduct);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private PagedResponse<ProductResponse> createPagedResponse(Page<Product> products) {
        List<ProductResponse> productResponses = products.getContent()
                .stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                productResponses,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isLast()
        );
    }
}
