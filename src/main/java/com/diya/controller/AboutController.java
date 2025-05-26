
package com.diya.controller;

import com.diya.dto.request.AboutPageRequest;
import com.diya.dto.response.AboutPageResponse;
import com.diya.service.AboutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/about")
@RequiredArgsConstructor
public class AboutController {

    private final AboutService aboutService;

    @GetMapping
    public ResponseEntity<AboutPageResponse> getActivePage() {
        return ResponseEntity.ok(aboutService.getActivePage());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AboutPageResponse> createPage(@Valid @RequestBody AboutPageRequest aboutPageRequest) {
        return ResponseEntity.ok(aboutService.createPage(aboutPageRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AboutPageResponse> updatePage(
            @PathVariable Long id, 
            @Valid @RequestBody AboutPageRequest aboutPageRequest) {
        return ResponseEntity.ok(aboutService.updatePage(id, aboutPageRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePage(@PathVariable Long id) {
        aboutService.deletePage(id);
        return ResponseEntity.noContent().build();
    }
}
