package com.smartheritage.backend.heritage.service;

import com.smartheritage.backend.common.BadRequestException;
import com.smartheritage.backend.heritage.dto.AnalyticsDashboard;
import com.smartheritage.backend.heritage.dto.ArtifactPopularity;
import com.smartheritage.backend.heritage.repository.VisitorFeedbackRepository;
import com.smartheritage.backend.heritage.repository.VisitorInteractionRepository;
import com.smartheritage.backend.identity.repository.UserAccountRepository;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {

    private final UserAccountRepository users;
    private final VisitorInteractionRepository interactions;
    private final VisitorFeedbackRepository feedback;

    public AnalyticsService(
            UserAccountRepository users,
            VisitorInteractionRepository interactions,
            VisitorFeedbackRepository feedback) {
        this.users = users;
        this.interactions = interactions;
        this.feedback = feedback;
    }

    @Transactional(readOnly = true)
    public AnalyticsDashboard dashboard(String period) {
        String normalized = period == null ? "daily" : period.trim().toLowerCase(Locale.ROOT);
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate start = switch (normalized) {
            case "daily" -> today;
            case "weekly" -> today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case "monthly" -> today.withDayOfMonth(1);
            default -> throw new BadRequestException("period must be daily, weekly, or monthly");
        };
        Instant from = start.atStartOfDay(ZoneOffset.UTC).toInstant();
        List<ArtifactPopularity> popular = interactions.findPopularArtifacts(from).stream()
                .limit(10)
                .map(row -> new ArtifactPopularity(
                        ((Number) row[0]).longValue(), (String) row[1], ((Number) row[2]).longValue()))
                .toList();
        Map<String, Long> languages = new LinkedHashMap<>();
        interactions.countByLanguageSince(from).forEach(row ->
                languages.put((String) row[0], ((Number) row[1]).longValue()));
        return new AnalyticsDashboard(normalized, from,
                users.countByRole_NameIgnoreCaseAndActiveTrue("VISITOR"),
                interactions.countDistinctVisitorsSince(from),
                interactions.countByHappenedAtAfter(from),
                Math.round(interactions.averageDurationSince(from)),
                Math.round(feedback.averageApprovedRating() * 100.0) / 100.0,
                feedback.countByStatus("PENDING"), popular, languages);
    }
}
