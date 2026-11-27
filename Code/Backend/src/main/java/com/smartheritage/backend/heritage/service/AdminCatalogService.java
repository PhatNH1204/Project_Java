package com.smartheritage.backend.heritage.service;

import com.smartheritage.backend.common.ConflictException;
import com.smartheritage.backend.common.NotFoundException;
import com.smartheritage.backend.heritage.dto.ArtifactRequest;
import com.smartheritage.backend.heritage.dto.ArtifactView;
import com.smartheritage.backend.heritage.dto.BeaconRequest;
import com.smartheritage.backend.heritage.dto.BeaconView;
import com.smartheritage.backend.heritage.dto.ContentRequest;
import com.smartheritage.backend.heritage.dto.LanguageRequest;
import com.smartheritage.backend.heritage.dto.LanguageView;
import com.smartheritage.backend.heritage.dto.SiteRequest;
import com.smartheritage.backend.heritage.dto.SiteView;
import com.smartheritage.backend.heritage.entity.Artifact;
import com.smartheritage.backend.heritage.entity.ArtifactContent;
import com.smartheritage.backend.heritage.entity.BeaconDevice;
import com.smartheritage.backend.heritage.entity.HeritageSite;
import com.smartheritage.backend.heritage.entity.SupportedLanguage;
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
public class AdminCatalogService {

    private final HeritageSiteRepository sites;
    private final ArtifactRepository artifacts;
    private final ArtifactContentRepository contents;
    private final BeaconDeviceRepository beacons;
    private final SupportedLanguageRepository languages;
    private final CatalogService catalog;

    public AdminCatalogService(
            HeritageSiteRepository sites,
            ArtifactRepository artifacts,
            ArtifactContentRepository contents,
            BeaconDeviceRepository beacons,
            SupportedLanguageRepository languages,
            CatalogService catalog) {
        this.sites = sites;
        this.artifacts = artifacts;
        this.contents = contents;
        this.beacons = beacons;
        this.languages = languages;
        this.catalog = catalog;
    }

    @Transactional(readOnly = true)
    public List<SiteView> sites() {
        return sites.findAll().stream()
                .map(site -> new SiteView(site.getId(), site.getName(), site.getDescription(),
                        site.getAddress(), site.getLatitude(), site.getLongitude(), site.isActive()))
                .toList();
    }

    @Transactional
    public SiteView createSite(SiteRequest request) {
        HeritageSite site = sites.save(new HeritageSite(
                request.name().trim(), valueOrEmpty(request.description()),
                valueOrEmpty(request.address()), request.latitude(), request.longitude()));
        if (!request.active()) {
            site.deactivate();
        }
        return toSiteView(site);
    }

    @Transactional
    public SiteView updateSite(Long id, SiteRequest request) {
        HeritageSite site = findSite(id);
        site.update(request.name().trim(), valueOrEmpty(request.description()),
                valueOrEmpty(request.address()), request.latitude(), request.longitude(), request.active());
        if (!site.isActive()) {
            artifacts.findBySite_IdOrderByCodeAsc(id).forEach(artifact -> {
                artifact.deactivate();
                beacons.findByArtifact_Id(artifact.getId()).forEach(BeaconDevice::deactivate);
            });
        }
        return toSiteView(site);
    }

    @Transactional
    public void deleteSite(Long id) {
        HeritageSite site = findSite(id);
        site.deactivate();
        artifacts.findBySite_IdOrderByCodeAsc(id).forEach(artifact -> {
            artifact.deactivate();
            beacons.findByArtifact_Id(artifact.getId()).forEach(BeaconDevice::deactivate);
        });
    }

    @Transactional(readOnly = true)
    public List<ArtifactView> artifacts(String languageCode) {
        return artifacts.findAll().stream()
                .map(artifact -> catalog.toView(artifact, languageCode))
                .toList();
    }

    @Transactional
    public ArtifactView createArtifact(ArtifactRequest request) {
        HeritageSite site = findSite(request.siteId());
        if (!site.isActive()) {
            throw new ConflictException("Cannot add an artifact to an inactive heritage site");
        }
        String code = request.code().trim().toUpperCase(Locale.ROOT);
        if (artifacts.existsByCodeIgnoreCase(code)) {
            throw new ConflictException("Artifact code is already in use");
        }
        requireLanguage(request.languageCode());
        Artifact artifact = artifacts.save(new Artifact(site, code,
                valueOrEmpty(request.locationName()), request.latitude(), request.longitude()));
        artifact.update(site, code, valueOrEmpty(request.locationName()),
                request.latitude(), request.longitude(), request.active());
        contents.save(new ArtifactContent(artifact, normalizeLanguage(request.languageCode()),
                request.title().trim(), request.description().trim(),
                request.imageUrl(), request.videoUrl(), request.audioUrl()));
        return catalog.toView(artifact, request.languageCode());
    }

    @Transactional
    public ArtifactView updateArtifact(Long id, ArtifactRequest request) {
        Artifact artifact = findArtifact(id);
        HeritageSite site = findSite(request.siteId());
        String code = request.code().trim().toUpperCase(Locale.ROOT);
        if (artifacts.existsByCodeIgnoreCaseAndIdNot(code, id)) {
            throw new ConflictException("Artifact code is already in use");
        }
        requireLanguage(request.languageCode());
        artifact.update(site, code, valueOrEmpty(request.locationName()),
                request.latitude(), request.longitude(), request.active() && site.isActive());
        saveContent(artifact, normalizeLanguage(request.languageCode()), request.title(),
                request.description(), request.imageUrl(), request.videoUrl(), request.audioUrl());
        return catalog.toView(artifact, request.languageCode());
    }

    @Transactional
    public ArtifactView updateContent(Long artifactId, String languageCode, ContentRequest request) {
        Artifact artifact = findArtifact(artifactId);
        String language = normalizeLanguage(languageCode);
        requireLanguage(language);
        saveContent(artifact, language, request.title(), request.description(),
                request.imageUrl(), request.videoUrl(), request.audioUrl());
        return catalog.toView(artifact, language);
    }

    @Transactional
    public void deleteArtifact(Long id) {
        Artifact artifact = findArtifact(id);
        artifact.deactivate();
        beacons.findByArtifact_Id(id).forEach(BeaconDevice::deactivate);
    }

    @Transactional(readOnly = true)
    public List<BeaconView> beacons() {
        return beacons.findAllByOrderByIdAsc().stream().map(this::toBeaconView).toList();
    }

    @Transactional
    public BeaconView createBeacon(BeaconRequest request) {
        Artifact artifact = request.artifactId() == null ? null : findActiveArtifact(request.artifactId());
        BeaconDevice beacon = beacons.save(new BeaconDevice(
                request.beaconUuid().toLowerCase(Locale.ROOT), request.major(), request.minor(),
                artifact, valueOrEmpty(request.placement()), request.latitude(),
                request.longitude(), request.txPower()));
        if (!request.active()) {
            beacon.deactivate();
        }
        return toBeaconView(beacon);
    }

    @Transactional
    public BeaconView updateBeacon(Long id, BeaconRequest request) {
        BeaconDevice beacon = findBeacon(id);
        Artifact artifact = request.artifactId() == null ? null : findActiveArtifact(request.artifactId());
        beacon.update(request.beaconUuid().toLowerCase(Locale.ROOT), request.major(), request.minor(),
                artifact, valueOrEmpty(request.placement()), request.latitude(),
                request.longitude(), request.txPower(), request.active());
        return toBeaconView(beacon);
    }

    @Transactional
    public BeaconView assignBeacon(Long beaconId, Long artifactId) {
        BeaconDevice beacon = findBeacon(beaconId);
        beacon.assign(findActiveArtifact(artifactId));
        return toBeaconView(beacon);
    }

    @Transactional
    public void deleteBeacon(Long id) {
        findBeacon(id).deactivate();
    }

    @Transactional(readOnly = true)
    public List<LanguageView> languages() {
        return languages.findAll().stream()
                .map(language -> new LanguageView(language.getCode(), language.getName()))
                .toList();
    }

    @Transactional
    public LanguageView saveLanguage(LanguageRequest request) {
        String code = normalizeLanguage(request.code());
        SupportedLanguage language = languages.findById(code)
                .orElseGet(() -> new SupportedLanguage(code, request.name().trim()));
        language.updateName(request.name().trim());
        language = languages.save(language);
        return new LanguageView(language.getCode(), language.getName());
    }

    @Transactional
    public void deleteLanguage(String code) {
        SupportedLanguage language = languages.findById(normalizeLanguage(code))
                .orElseThrow(() -> new NotFoundException("Language not found"));
        language.deactivate();
    }

    private void saveContent(Artifact artifact, String language, String title,
            String description, String imageUrl, String videoUrl, String audioUrl) {
        ArtifactContent content = contents
                .findByArtifact_IdAndLanguageCodeIgnoreCase(artifact.getId(), language)
                .orElseGet(() -> new ArtifactContent(artifact, language,
                        title.trim(), description.trim(), imageUrl, videoUrl, audioUrl));
        content.update(title.trim(), description.trim(), imageUrl, videoUrl, audioUrl);
        contents.save(content);
    }

    private void requireLanguage(String code) {
        languages.findByCodeIgnoreCaseAndActiveTrue(normalizeLanguage(code))
                .orElseThrow(() -> new NotFoundException("Supported language not found"));
    }

    private HeritageSite findSite(Long id) {
        return sites.findById(id).orElseThrow(() -> new NotFoundException("Heritage site not found"));
    }

    private Artifact findArtifact(Long id) {
        return artifacts.findById(id).orElseThrow(() -> new NotFoundException("Artifact not found"));
    }

    private Artifact findActiveArtifact(Long id) {
        return artifacts.findByIdAndActiveTrue(id)
                .filter(artifact -> artifact.getSite().isActive())
                .orElseThrow(() -> new NotFoundException("Active artifact not found"));
    }

    private BeaconDevice findBeacon(Long id) {
        return beacons.findById(id).orElseThrow(() -> new NotFoundException("Beacon not found"));
    }

    private SiteView toSiteView(HeritageSite site) {
        return new SiteView(site.getId(), site.getName(), site.getDescription(),
                site.getAddress(), site.getLatitude(), site.getLongitude(), site.isActive());
    }

    private BeaconView toBeaconView(BeaconDevice beacon) {
        Artifact artifact = beacon.getArtifact();
        return new BeaconView(beacon.getId(), beacon.getBeaconUuid(), beacon.getMajor(),
                beacon.getMinor(), artifact == null ? null : artifact.getId(),
                artifact == null ? null : artifact.getCode(), beacon.getPlacement(),
                beacon.getLatitude(), beacon.getLongitude(), beacon.getTxPower(), beacon.isActive());
    }

    private String normalizeLanguage(String code) {
        return code.trim().toLowerCase(Locale.ROOT);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
