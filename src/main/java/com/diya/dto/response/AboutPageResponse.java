
package com.diya.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AboutPageResponse {
    private Long id;
    private String title;
    private String content;
    private String heroImage;
    private String missionStatement;
    private String visionStatement;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
