package com.smartheritage.backend.heritage.dto;

import java.time.Instant;

public record InteractionView(
        Long id,
        String eventKey,
        Long artifactId,
        String artifactCode,
        Long beaconId,
        String languageCode,
        int durationSeconds,
        Instant happenedAt) {
}
