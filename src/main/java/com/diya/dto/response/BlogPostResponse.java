
package com.diya.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostResponse {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String featuredImage;
    private String excerpt;
    private UserResponse author;
    private List<String> tags = new ArrayList<>();
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}
