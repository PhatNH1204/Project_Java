package com.smartheritage.backend.heritage.controller;

import com.smartheritage.backend.common.ApiResponse;
import com.smartheritage.backend.heritage.dto.ArtifactView;
import com.smartheritage.backend.heritage.dto.BeaconResolution;
import com.smartheritage.backend.heritage.dto.LanguageView;
import com.smartheritage.backend.heritage.dto.SiteView;
import com.smartheritage.backend.heritage.service.CatalogService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CatalogController {

    private final CatalogService catalog;

    public CatalogController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/sites")
    public ApiResponse<List<SiteView>> sites() {
        return ApiResponse.of(catalog.sites());
    }

    @GetMapping("/sites/{id}")
    public ApiResponse<SiteView> site(@PathVariable Long id) {
        return ApiResponse.of(catalog.site(id));
    }

    @GetMapping("/sites/{siteId}/artifacts")
    public ApiResponse<List<ArtifactView>> siteArtifacts(
            @PathVariable Long siteId, @RequestParam(defaultValue = "vi") String lang) {
        return ApiResponse.of(catalog.artifacts(siteId, null, lang));
    }

    @GetMapping("/artifacts")
    public ApiResponse<List<ArtifactView>> artifacts(
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "vi") String lang) {
        return ApiResponse.of(catalog.artifacts(siteId, search, lang));
    }

    @GetMapping("/artifacts/{id}")
    public ApiResponse<ArtifactView> artifact(
            @PathVariable Long id, @RequestParam(defaultValue = "vi") String lang) {
        return ApiResponse.of(catalog.artifact(id, lang));
    }

    @GetMapping("/languages")
    public ApiResponse<List<LanguageView>> languages() {
        return ApiResponse.of(catalog.languages());
    }

    @GetMapping("/beacons/resolve")
    public ApiResponse<BeaconResolution> resolveBeacon(
            @RequestParam String uuid,
            @RequestParam int major,
            @RequestParam int minor,
            @RequestParam(defaultValue = "vi") String lang) {
        return ApiResponse.of(catalog.resolveBeacon(uuid, major, minor, lang));
    }
}
