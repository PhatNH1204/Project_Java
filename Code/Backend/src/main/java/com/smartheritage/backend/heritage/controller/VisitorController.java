package com.smartheritage.backend.heritage.controller;

import com.smartheritage.backend.common.ApiResponse;
import com.smartheritage.backend.heritage.dto.ArtifactView;
import com.smartheritage.backend.heritage.dto.FeedbackRequest;
import com.smartheritage.backend.heritage.dto.FeedbackView;
import com.smartheritage.backend.heritage.dto.InteractionRequest;
import com.smartheritage.backend.heritage.dto.InteractionResult;
import com.smartheritage.backend.heritage.dto.InteractionView;
import com.smartheritage.backend.heritage.service.VisitorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VisitorController {

    private final VisitorService visitors;

    public VisitorController(VisitorService visitors) {
        this.visitors = visitors;
    }

    @GetMapping("/favorites")
    public ApiResponse<List<ArtifactView>> favorites(
            @AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "vi") String lang) {
        return ApiResponse.of(visitors.favorites(userId(jwt), lang));
    }

    @PostMapping("/favorites/{artifactId}")
    public ResponseEntity<ApiResponse<ArtifactView>> addFavorite(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long artifactId,
            @RequestParam(defaultValue = "vi") String lang) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.of(visitors.addFavorite(userId(jwt), artifactId, lang)));
    }

    @DeleteMapping("/favorites/{artifactId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal Jwt jwt, @PathVariable Long artifactId) {
        visitors.removeFavorite(userId(jwt), artifactId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    public ApiResponse<List<InteractionView>> history(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.of(visitors.history(userId(jwt)));
    }

    @PostMapping("/interactions")
    public ApiResponse<InteractionResult> recordInteraction(
            @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody InteractionRequest request) {
        return ApiResponse.of(visitors.recordInteraction(userId(jwt), request));
    }

    @PostMapping("/feedback")
    public ResponseEntity<ApiResponse<FeedbackView>> submitFeedback(
            @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.of(visitors.submitFeedback(userId(jwt), request)));
    }

    @GetMapping("/artifacts/{artifactId}/feedback")
    public ApiResponse<List<FeedbackView>> approvedFeedback(@PathVariable Long artifactId) {
        return ApiResponse.of(visitors.approvedFeedback(artifactId));
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
