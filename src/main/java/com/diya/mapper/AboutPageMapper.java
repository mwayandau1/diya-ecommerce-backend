
package com.diya.mapper;

import com.diya.dto.request.AboutPageRequest;
import com.diya.dto.response.AboutPageResponse;
import com.diya.model.AboutPage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AboutPageMapper {

    public AboutPageResponse toAboutPageResponse(AboutPage aboutPage) {
        return AboutPageResponse.builder()
                .id(aboutPage.getId())
                .title(aboutPage.getTitle())
                .content(aboutPage.getContent())
                .heroImage(aboutPage.getHeroImage())
                .missionStatement(aboutPage.getMissionStatement())
                .visionStatement(aboutPage.getVisionStatement())
                .active(aboutPage.isActive())
                .createdAt(aboutPage.getCreatedAt())
                .updatedAt(aboutPage.getUpdatedAt())
                .build();
    }

    public AboutPage toAboutPage(AboutPageRequest request) {
        AboutPage aboutPage = new AboutPage();
        updateAboutPageFromRequest(request, aboutPage);
        return aboutPage;
    }

    public void updateAboutPageFromRequest(AboutPageRequest request, AboutPage aboutPage) {
        aboutPage.setTitle(request.getTitle());
        aboutPage.setContent(request.getContent());
        aboutPage.setHeroImage(request.getHeroImage());
        aboutPage.setMissionStatement(request.getMissionStatement());
        aboutPage.setVisionStatement(request.getVisionStatement());
        aboutPage.setActive(request.isActive());
        
        if (aboutPage.getCreatedAt() == null) {
            aboutPage.setCreatedAt(LocalDateTime.now());
        }
        aboutPage.setUpdatedAt(LocalDateTime.now());
    }
}
