package com.smartheritage.backend.heritage.controller;

import com.smartheritage.backend.common.ApiResponse;
import com.smartheritage.backend.heritage.dto.ArtifactRequest;
import com.smartheritage.backend.heritage.dto.ArtifactView;
import com.smartheritage.backend.heritage.dto.BeaconAssignmentRequest;
import com.smartheritage.backend.heritage.dto.BeaconRequest;
import com.smartheritage.backend.heritage.dto.BeaconView;
import com.smartheritage.backend.heritage.dto.ContentRequest;
import com.smartheritage.backend.heritage.dto.LanguageRequest;
import com.smartheritage.backend.heritage.dto.LanguageView;
import com.smartheritage.backend.heritage.dto.SiteRequest;
import com.smartheritage.backend.heritage.dto.SiteView;
import com.smartheritage.backend.heritage.service.AdminCatalogService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCatalogController {

    private final AdminCatalogService admin;

    public AdminCatalogController(AdminCatalogService admin) {
        this.admin = admin;
    }

    @GetMapping("/sites")
    public ApiResponse<List<SiteView>> sites() {
        return ApiResponse.of(admin.sites());
    }

    @PostMapping("/sites")
    public ResponseEntity<ApiResponse<SiteView>> createSite(@Valid @RequestBody SiteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(admin.createSite(request)));
    }

    @PutMapping("/sites/{id}")
    public ApiResponse<SiteView> updateSite(
            @PathVariable Long id, @Valid @RequestBody SiteRequest request) {
        return ApiResponse.of(admin.updateSite(id, request));
    }

    @DeleteMapping("/sites/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        admin.deleteSite(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/artifacts")
    public ApiResponse<List<ArtifactView>> artifacts(
            @RequestParam(defaultValue = "vi") String lang) {
        return ApiResponse.of(admin.artifacts(lang));
    }

    @PostMapping("/artifacts")
    public ResponseEntity<ApiResponse<ArtifactView>> createArtifact(
            @Valid @RequestBody ArtifactRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(admin.createArtifact(request)));
    }

    @PutMapping("/artifacts/{id}")
    public ApiResponse<ArtifactView> updateArtifact(
            @PathVariable Long id, @Valid @RequestBody ArtifactRequest request) {
        return ApiResponse.of(admin.updateArtifact(id, request));
    }

    @PutMapping("/artifacts/{id}/content/{languageCode}")
    public ApiResponse<ArtifactView> updateContent(
            @PathVariable Long id,
            @PathVariable String languageCode,
            @Valid @RequestBody ContentRequest request) {
        return ApiResponse.of(admin.updateContent(id, languageCode, request));
    }

    @DeleteMapping("/artifacts/{id}")
    public ResponseEntity<Void> deleteArtifact(@PathVariable Long id) {
        admin.deleteArtifact(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/beacons")
    public ApiResponse<List<BeaconView>> beacons() {
        return ApiResponse.of(admin.beacons());
    }

    @PostMapping("/beacons")
    public ResponseEntity<ApiResponse<BeaconView>> createBeacon(
            @Valid @RequestBody BeaconRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(admin.createBeacon(request)));
    }

    @PutMapping("/beacons/{id}")
    public ApiResponse<BeaconView> updateBeacon(
            @PathVariable Long id, @Valid @RequestBody BeaconRequest request) {
        return ApiResponse.of(admin.updateBeacon(id, request));
    }

    @PostMapping("/beacons/{id}/assign")
    public ApiResponse<BeaconView> assignBeacon(
            @PathVariable Long id, @Valid @RequestBody BeaconAssignmentRequest request) {
        return ApiResponse.of(admin.assignBeacon(id, request.artifactId()));
    }

    @DeleteMapping("/beacons/{id}")
    public ResponseEntity<Void> deleteBeacon(@PathVariable Long id) {
        admin.deleteBeacon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/languages")
    public ApiResponse<List<LanguageView>> languages() {
        return ApiResponse.of(admin.languages());
    }

    @PostMapping("/languages")
    public ResponseEntity<ApiResponse<LanguageView>> saveLanguage(
            @Valid @RequestBody LanguageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(admin.saveLanguage(request)));
    }

    @DeleteMapping("/languages/{code}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable String code) {
        admin.deleteLanguage(code);
        return ResponseEntity.noContent().build();
    }
}
