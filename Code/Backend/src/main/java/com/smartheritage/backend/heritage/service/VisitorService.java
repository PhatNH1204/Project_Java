package com.smartheritage.backend.heritage.service;

import com.smartheritage.backend.common.BadRequestException;
import com.smartheritage.backend.common.ConflictException;
import com.smartheritage.backend.common.NotFoundException;
import com.smartheritage.backend.heritage.dto.ArtifactView;
import com.smartheritage.backend.heritage.dto.FeedbackRequest;
import com.smartheritage.backend.heritage.dto.FeedbackView;
import com.smartheritage.backend.heritage.dto.InteractionRequest;
import com.smartheritage.backend.heritage.dto.InteractionResult;
import com.smartheritage.backend.heritage.dto.InteractionView;
import com.smartheritage.backend.heritage.entity.Artifact;
import com.smartheritage.backend.heritage.entity.BeaconDevice;
import com.smartheritage.backend.heritage.entity.Favorite;
import com.smartheritage.backend.heritage.entity.VisitorFeedback;
import com.smartheritage.backend.heritage.entity.VisitorInteraction;
import com.smartheritage.backend.heritage.repository.ArtifactRepository;
import com.smartheritage.backend.heritage.repository.BeaconDeviceRepository;
import com.smartheritage.backend.heritage.repository.FavoriteRepository;
import com.smartheritage.backend.heritage.repository.SupportedLanguageRepository;
import com.smartheritage.backend.heritage.repository.VisitorFeedbackRepository;
import com.smartheritage.backend.heritage.repository.VisitorInteractionRepository;
import com.smartheritage.backend.identity.entity.UserAccount;
import com.smartheritage.backend.identity.repository.UserAccountRepository;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisitorService {

    private final UserAccountRepository users;
    private final ArtifactRepository artifacts;
    private final BeaconDeviceRepository beacons;
    private final FavoriteRepository favorites;
    private final VisitorInteractionRepository interactions;
    private final VisitorFeedbackRepository feedback;
    private final SupportedLanguageRepository languages;
    private final CatalogService catalog;

    public VisitorService(
            UserAccountRepository users,
            ArtifactRepository artifacts,
            BeaconDeviceRepository beacons,
            FavoriteRepository favorites,
            VisitorInteractionRepository interactions,
            VisitorFeedbackRepository feedback,
            SupportedLanguageRepository languages,
            CatalogService catalog) {
        this.users = users;
        this.artifacts = artifacts;
        this.beacons = beacons;
        this.favorites = favorites;
        this.interactions = interactions;
        this.feedback = feedback;
        this.languages = languages;
        this.catalog = catalog;
    }

    @Transactional(readOnly = true)
    public List<ArtifactView> favorites(Long userId, String languageCode) {
        return favorites.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .filter(favorite -> favorite.getArtifact().isActive()
                        && favorite.getArtifact().getSite().isActive())
                .map(favorite -> catalog.toView(favorite.getArtifact(), languageCode))
                .toList();
    }

    @Transactional
    public ArtifactView addFavorite(Long userId, Long artifactId, String languageCode) {
        UserAccount user = findUser(userId);
        Artifact artifact = findActiveArtifact(artifactId);
        if (favorites.findByUser_IdAndArtifact_Id(userId, artifactId).isEmpty()) {
            favorites.save(new Favorite(user, artifact));
        }
        return catalog.toView(artifact, languageCode);
    }

    @Transactional
    public void removeFavorite(Long userId, Long artifactId) {
        favorites.findByUser_IdAndArtifact_Id(userId, artifactId).ifPresent(favorites::delete);
    }

    @Transactional(readOnly = true)
    public List<InteractionView> history(Long userId) {
        return interactions.findByUser_IdOrderByHappenedAtDesc(userId).stream()
                .map(this::toInteractionView)
                .toList();
    }

    @Transactional
    public InteractionResult recordInteraction(Long userId, InteractionRequest request) {
        UserAccount user = findUser(userId);
        Artifact artifact = findActiveArtifact(request.artifactId());
        String eventKey;
        try {
            eventKey = UUID.fromString(request.eventKey()).toString();
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("eventKey must be a UUID");
        }
        var existing = interactions.findByUser_IdAndEventKey(userId, eventKey);
        if (existing.isPresent()) {
            return new InteractionResult(toInteractionView(existing.get()), true);
        }

        BeaconDevice beacon = null;
        if (request.beaconId() != null) {
            beacon = beacons.findById(request.beaconId())
                    .filter(BeaconDevice::isActive)
                    .orElseThrow(() -> new NotFoundException("Active beacon not found"));
            if (beacon.getArtifact() == null
                    || !beacon.getArtifact().getId().equals(artifact.getId())) {
                throw new ConflictException("Beacon is not assigned to the requested artifact");
            }
        }
        String language = request.languageCode().trim().toLowerCase(Locale.ROOT);
        languages.findByCodeIgnoreCaseAndActiveTrue(language)
                .orElseThrow(() -> new NotFoundException("Supported language not found"));
        VisitorInteraction saved = interactions.save(new VisitorInteraction(
                eventKey, user, artifact, beacon, language, request.durationSeconds()));
        return new InteractionResult(toInteractionView(saved), false);
    }

    @Transactional
    public FeedbackView submitFeedback(Long userId, FeedbackRequest request) {
        UserAccount user = findUser(userId);
        Artifact artifact = findActiveArtifact(request.artifactId());
        String comment = request.comment() == null ? "" : request.comment().trim();
        VisitorFeedback saved = feedback.findByUser_IdAndArtifact_Id(userId, artifact.getId())
                .map(existing -> {
                    existing.updateRatingAndComment(request.rating(), comment);
                    return existing;
                })
                .orElseGet(() -> new VisitorFeedback(user, artifact, request.rating(), comment));
        return toFeedbackView(feedback.save(saved));
    }

    @Transactional(readOnly = true)
    public List<FeedbackView> approvedFeedback(Long artifactId) {
        findActiveArtifact(artifactId);
        return feedback.findByArtifact_IdAndStatusOrderByCreatedAtDesc(artifactId, "APPROVED")
                .stream().map(this::toFeedbackView).toList();
    }

    private UserAccount findUser(Long id) {
        return users.findById(id).filter(UserAccount::isActive)
                .orElseThrow(() -> new NotFoundException("Active user not found"));
    }

    private Artifact findActiveArtifact(Long id) {
        return artifacts.findByIdAndActiveTrue(id)
                .filter(value -> value.getSite().isActive())
                .orElseThrow(() -> new NotFoundException("Artifact not found"));
    }

    private InteractionView toInteractionView(VisitorInteraction interaction) {
        return new InteractionView(interaction.getId(), interaction.getEventKey(),
                interaction.getArtifact().getId(), interaction.getArtifact().getCode(),
                interaction.getBeacon() == null ? null : interaction.getBeacon().getId(),
                interaction.getLanguageCode(), interaction.getDurationSeconds(),
                interaction.getHappenedAt());
    }

    private FeedbackView toFeedbackView(VisitorFeedback item) {
        return new FeedbackView(item.getId(), item.getArtifact().getId(),
                item.getArtifact().getCode(), item.getUser().getId(),
                item.getUser().getUsername(), item.getRating(), item.getComment(),
                item.getStatus(), item.getCreatedAt());
    }
}
