
package com.diya.service;

import com.diya.dto.request.BlogPostRequest;
import com.diya.dto.response.BlogPostResponse;
import com.diya.dto.response.PagedResponse;
import com.diya.exception.ResourceNotFoundException;
import com.diya.mapper.BlogPostMapper;
import com.diya.model.BlogPost;
import com.diya.model.User;
import com.diya.repository.BlogPostRepository;
import com.diya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    private final BlogPostMapper blogPostMapper;

    public PagedResponse<BlogPostResponse> getAllPublishedPosts(Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findByPublishedTrue(pageable);
        return createPagedResponse(posts);
    }

    public PagedResponse<BlogPostResponse> getAllPosts(Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findAll(pageable);
        return createPagedResponse(posts);
    }

    public BlogPostResponse getPostById(Long id) {
        BlogPost post = findPostById(id);
        return blogPostMapper.toBlogPostResponse(post);
    }

    public BlogPostResponse getPostBySlug(String slug) {
        BlogPost post = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with slug: " + slug));
        return blogPostMapper.toBlogPostResponse(post);
    }

    public PagedResponse<BlogPostResponse> searchPosts(String keyword, Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.search(keyword, pageable);
        return createPagedResponse(posts);
    }

    public PagedResponse<BlogPostResponse> getPostsByTag(String tag, Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findByTag(tag, pageable);
        return createPagedResponse(posts);
    }

    public List<String> getAllTags() {
        return blogPostRepository.findAllTags();
    }

    @Transactional
    public BlogPostResponse createPost(String username, BlogPostRequest blogPostRequest) {
        User author = findUserByEmail(username);
        
        BlogPost blogPost = blogPostMapper.toBlogPost(blogPostRequest);
        blogPost.setAuthor(author);
        
        if (blogPost.isPublished()) {
            blogPost.setPublishedAt(LocalDateTime.now());
        }
        
        BlogPost savedPost = blogPostRepository.save(blogPost);
        return blogPostMapper.toBlogPostResponse(savedPost);
    }

    @Transactional
    public BlogPostResponse updatePost(Long id, BlogPostRequest blogPostRequest) {
        BlogPost existingPost = findPostById(id);
        
        boolean wasPublished = existingPost.isPublished();
        
        blogPostMapper.updateBlogPostFromRequest(blogPostRequest, existingPost);
        
        // Set published date if post is being published for the first time
        if (!wasPublished && existingPost.isPublished()) {
            existingPost.setPublishedAt(LocalDateTime.now());
        }
        
        BlogPost updatedPost = blogPostRepository.save(existingPost);
        return blogPostMapper.toBlogPostResponse(updatedPost);
    }

    @Transactional
    public void deletePost(Long id) {
        BlogPost post = findPostById(id);
        blogPostRepository.delete(post);
    }

    private BlogPost findPostById(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private PagedResponse<BlogPostResponse> createPagedResponse(Page<BlogPost> posts) {
        List<BlogPostResponse> postResponses = posts.getContent()
                .stream()
                .map(blogPostMapper::toBlogPostResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                postResponses,
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.isLast()
        );
    }
}
