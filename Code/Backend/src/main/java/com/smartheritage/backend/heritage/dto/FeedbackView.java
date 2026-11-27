package com.smartheritage.backend.heritage.dto;

import java.time.Instant;

public record FeedbackView(
        Long id,
        Long artifactId,
        String artifactCode,
        Long userId,
        String username,
        int rating,
        String comment,
        String status,
        Instant createdAt) {
}
