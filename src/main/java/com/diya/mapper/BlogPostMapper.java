
package com.diya.mapper;


import com.diya.dto.request.BlogPostRequest;
import com.diya.dto.response.BlogPostResponse;
import com.diya.model.BlogPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BlogPostMapper {

    private final UserMapper userMapper;

    public BlogPostResponse toBlogPostResponse(BlogPost blogPost) {
        return BlogPostResponse.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .slug(blogPost.getSlug())
                .content(blogPost.getContent())
                .featuredImage(blogPost.getFeaturedImage())
                .excerpt(blogPost.getExcerpt())
                .author(userMapper.toUserResponse(blogPost.getAuthor()))
                .tags(blogPost.getTags())
                .published(blogPost.isPublished())
                .createdAt(blogPost.getCreatedAt())
                .updatedAt(blogPost.getUpdatedAt())
                .publishedAt(blogPost.getPublishedAt())
                .build();
    }

    public BlogPost toBlogPost(BlogPostRequest blogPostRequest) {
        BlogPost blogPost = new BlogPost();
        updateBlogPostFromRequest(blogPostRequest, blogPost);
        return blogPost;
    }

    public void updateBlogPostFromRequest(BlogPostRequest request, BlogPost blogPost) {
        blogPost.setTitle(request.getTitle());
        
        if (request.getSlug() != null) {
            blogPost.setSlug(request.getSlug());
        } else if (blogPost.getSlug() == null) {
            String slug = request.getTitle()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "-")
                    .replaceAll("-+", "-")
                    .replaceAll("^-|-$", "");
            blogPost.setSlug(slug);
        }
        
        blogPost.setContent(request.getContent());
        blogPost.setFeaturedImage(request.getFeaturedImage());
        blogPost.setExcerpt(request.getExcerpt());
        blogPost.setTags(request.getTags());
        blogPost.setPublished(request.isPublished());
        
        if (blogPost.getCreatedAt() == null) {
            blogPost.setCreatedAt(LocalDateTime.now());
        }
        blogPost.setUpdatedAt(LocalDateTime.now());
    }
}
