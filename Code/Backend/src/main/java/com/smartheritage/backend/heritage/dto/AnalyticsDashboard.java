package com.smartheritage.backend.heritage.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record AnalyticsDashboard(
        String period,
        Instant from,
        long totalVisitors,
        long visitorsInPeriod,
        long interactionsInPeriod,
        long averageEngagementSeconds,
        double averageRating,
        long pendingFeedback,
        List<ArtifactPopularity> popularArtifacts,
        Map<String, Long> languageDistribution) {
}
