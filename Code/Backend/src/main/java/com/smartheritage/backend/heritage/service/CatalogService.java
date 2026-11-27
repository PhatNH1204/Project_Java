package com.smartheritage.backend.heritage.service;

import com.smartheritage.backend.common.NotFoundException;
import com.smartheritage.backend.heritage.dto.ArtifactView;
import com.smartheritage.backend.heritage.dto.BeaconResolution;
import com.smartheritage.backend.heritage.dto.ContentView;
import com.smartheritage.backend.heritage.dto.LanguageView;
import com.smartheritage.backend.heritage.dto.SiteView;
import com.smartheritage.backend.heritage.entity.Artifact;
import com.smartheritage.backend.heritage.entity.ArtifactContent;
import com.smartheritage.backend.heritage.entity.BeaconDevice;
import com.smartheritage.backend.heritage.repository.ArtifactContentRepository;
import com.smartheritage.backend.heritage.repository.ArtifactRepository;
import com.smartheritage.backend.heritage.repository.BeaconDeviceRepository;
import com.smartheritage.backend.heritage.repository.HeritageSiteRepository;
import com.smartheritage.backend.heritage.repository.SupportedLanguageRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {

    private final HeritageSiteRepository sites;
    private final ArtifactRepository artifacts;
    private final ArtifactContentRepository contents;
    private final BeaconDeviceRepository beacons;
    private final SupportedLanguageRepository languages;

    public CatalogService(
            HeritageSiteRepository sites,
            ArtifactRepository artifacts,
            ArtifactContentRepository contents,
            BeaconDeviceRepository beacons,
            SupportedLanguageRepository languages) {
        this.sites = sites;
        this.artifacts = artifacts;
        this.contents = contents;
        this.beacons = beacons;
        this.languages = languages;
    }

    @Transactional(readOnly = true)
    public List<SiteView> sites() {
        return sites.findByActiveTrueOrderByNameAsc().stream()
                .map(site -> new SiteView(site.getId(), site.getName(), site.getDescription(),
                        site.getAddress(), site.getLatitude(), site.getLongitude(), site.isActive()))
                .toList();
    }

    @Transactional(readOnly = true)
    public SiteView site(Long id) {
        var site = sites.findById(id).filter(value -> value.isActive())
                .orElseThrow(() -> new NotFoundException("Heritage site not found"));
        return new SiteView(site.getId(), site.getName(), site.getDescription(),
                site.getAddress(), site.getLatitude(), site.getLongitude(), site.isActive());
    }

    @Transactional(readOnly = true)
    public List<ArtifactView> artifacts(Long siteId, String search, String languageCode) {
        String term = search == null || search.isBlank() ? null : search.trim();
        return artifacts.searchActive(siteId, term).stream()
                .filter(artifact -> artifact.getSite().isActive())
                .map(artifact -> toView(artifact, languageCode))
                .toList();
    }

    @Transactional(readOnly = true)
    public ArtifactView artifact(Long id, String languageCode) {
        Artifact artifact = artifacts.findByIdAndActiveTrue(id)
                .filter(value -> value.getSite().isActive())
                .orElseThrow(() -> new NotFoundException("Artifact not found"));
        return toView(artifact, languageCode);
    }

    @Transactional(readOnly = true)
    public List<LanguageView> languages() {
        return languages.findByActiveTrueOrderByCodeAsc().stream()
                .map(language -> new LanguageView(language.getCode(), language.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public BeaconResolution resolveBeacon(String beaconUuid, int major, int minor, String languageCode) {
        BeaconDevice beacon = beacons.findByBeaconUuidIgnoreCaseAndMajorAndMinorAndActiveTrue(
                        beaconUuid, major, minor)
                .orElseThrow(() -> new NotFoundException("Active beacon was not found"));
        Artifact artifact = beacon.getArtifact();
        if (artifact == null || !artifact.isActive() || !artifact.getSite().isActive()) {
            throw new NotFoundException("The beacon is not assigned to an active artifact");
        }
        return new BeaconResolution(beacon.getId(), toView(artifact, languageCode));
    }

    @Transactional(readOnly = true)
    public ArtifactView toView(Artifact artifact, String languageCode) {
        ArtifactContent content = null;
        String preferred = languageCode == null || languageCode.isBlank()
                ? "vi" : languageCode.trim().toLowerCase(Locale.ROOT);
        if (languages.findByCodeIgnoreCaseAndActiveTrue(preferred).isPresent()) {
            content = contents.findByArtifact_IdAndLanguageCodeIgnoreCase(artifact.getId(), preferred)
                    .orElse(null);
        }
        if (content == null && !preferred.equals("vi")) {
            content = contents.findByArtifact_IdAndLanguageCodeIgnoreCase(artifact.getId(), "vi")
                    .orElse(null);
        }
        if (content == null) {
            content = contents.findByArtifact_IdOrderByLanguageCodeAsc(artifact.getId()).stream()
                    .findFirst().orElse(null);
        }
        ContentView contentView = content == null ? null : new ContentView(
                content.getLanguageCode(), content.getTitle(), content.getDescription(),
                content.getImageUrl(), content.getVideoUrl(), content.getAudioUrl());
        return new ArtifactView(artifact.getId(), artifact.getCode(), artifact.getSite().getId(),
                artifact.getSite().getName(), artifact.getLocationName(),
                artifact.getLatitude(), artifact.getLongitude(), contentView, artifact.isActive());
    }
}
