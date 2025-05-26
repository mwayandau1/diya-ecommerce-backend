
package com.diya.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String slug;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private String featuredImage;
    
    @NotBlank(message = "Excerpt is required")
    private String excerpt;
    
    private List<String> tags = new ArrayList<>();
    
    private boolean published = false;
}
