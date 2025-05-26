
package com.diya.service;

import com.diya.dto.request.AboutPageRequest;
import com.diya.dto.response.AboutPageResponse;
import com.diya.exception.ResourceNotFoundException;
import com.diya.mapper.AboutPageMapper;
import com.diya.model.AboutPage;
import com.diya.repository.AboutPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AboutService {

    private final AboutPageRepository aboutPageRepository;
    private final AboutPageMapper aboutPageMapper;

    public AboutPageResponse getActivePage() {
        AboutPage aboutPage = aboutPageRepository.findByActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("No active about page found"));
        return aboutPageMapper.toAboutPageResponse(aboutPage);
    }

    public List<AboutPageResponse> getAllPages() {
        return aboutPageRepository.findAll().stream()
                .map(aboutPageMapper::toAboutPageResponse)
                .collect(Collectors.toList());
    }

    public AboutPageResponse getPageById(Long id) {
        AboutPage aboutPage = findPageById(id);
        return aboutPageMapper.toAboutPageResponse(aboutPage);
    }

    @Transactional
    public AboutPageResponse createPage(AboutPageRequest request) {
        AboutPage aboutPage = aboutPageMapper.toAboutPage(request);
        
        // If this is set as active, deactivate all other pages
        if (aboutPage.isActive()) {
            deactivateAllPages();
        }
        
        AboutPage savedPage = aboutPageRepository.save(aboutPage);
        return aboutPageMapper.toAboutPageResponse(savedPage);
    }

    @Transactional
    public AboutPageResponse updatePage(Long id, AboutPageRequest request) {
        AboutPage existingPage = findPageById(id);
        
        aboutPageMapper.updateAboutPageFromRequest(request, existingPage);
        
        // If this is set as active, deactivate all other pages
        if (existingPage.isActive()) {
            deactivateAllPagesExcept(id);
        }
        
        AboutPage updatedPage = aboutPageRepository.save(existingPage);
        return aboutPageMapper.toAboutPageResponse(updatedPage);
    }

    @Transactional
    public void deletePage(Long id) {
        AboutPage aboutPage = findPageById(id);
        
        // Don't allow deleting the active page if it's the only one
        if (aboutPage.isActive() && aboutPageRepository.count() == 1) {
            throw new IllegalStateException("Cannot delete the only active about page");
        }
        
        aboutPageRepository.delete(aboutPage);
        
        // If we deleted the active page, make another one active
        if (aboutPage.isActive()) {
            aboutPageRepository.findAll().stream()
                    .findFirst()
                    .ifPresent(page -> {
                        page.setActive(true);
                        aboutPageRepository.save(page);
                    });
        }
    }

    private AboutPage findPageById(Long id) {
        return aboutPageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("About page not found with id: " + id));
    }

    private void deactivateAllPages() {
        aboutPageRepository.findAll().forEach(page -> {
            page.setActive(false);
            aboutPageRepository.save(page);
        });
    }

    private void deactivateAllPagesExcept(Long id) {
        aboutPageRepository.findAll().stream()
                .filter(page -> !page.getId().equals(id))
                .forEach(page -> {
                    page.setActive(false);
                    aboutPageRepository.save(page);
                });
    }
}
