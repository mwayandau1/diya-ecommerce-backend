
package com.diya.controller;

import com.diya.dto.request.BlogPostRequest;
import com.diya.dto.response.BlogPostResponse;
import com.diya.dto.response.PagedResponse;
import com.diya.service.BlogService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping("/posts")
    public ResponseEntity<PagedResponse<BlogPostResponse>> getAllPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(blogService.getAllPublishedPosts(pageable));
    }

    @GetMapping("/posts/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<BlogPostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(blogService.getAllPosts(pageable));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<BlogPostResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getPostById(id));
    }

    @GetMapping("/posts/slug/{slug}")
    public ResponseEntity<BlogPostResponse> getPostBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(blogService.getPostBySlug(slug));
    }

    @GetMapping("/posts/search")
    public ResponseEntity<PagedResponse<BlogPostResponse>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(blogService.searchPosts(keyword, pageable));
    }

    @GetMapping("/posts/tag/{tag}")
    public ResponseEntity<PagedResponse<BlogPostResponse>> getPostsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(blogService.getPostsByTag(tag, pageable));
    }

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        return ResponseEntity.ok(blogService.getAllTags());
    }

    @PostMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BlogPostResponse> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BlogPostRequest blogPostRequest) {
        return ResponseEntity.ok(blogService.createPost(userDetails.getUsername(), blogPostRequest));
    }

    @PutMapping("/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BlogPostResponse> updatePost(
            @PathVariable Long id, 
            @Valid @RequestBody BlogPostRequest blogPostRequest) {
        return ResponseEntity.ok(blogService.updatePost(id, blogPostRequest));
    }

    @DeleteMapping("/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        blogService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
