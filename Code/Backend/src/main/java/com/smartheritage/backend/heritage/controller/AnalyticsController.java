package com.smartheritage.backend.heritage.controller;

import com.smartheritage.backend.common.ApiResponse;
import com.smartheritage.backend.heritage.dto.AnalyticsDashboard;
import com.smartheritage.backend.heritage.service.AnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analytics;

    public AnalyticsController(AnalyticsService analytics) {
        this.analytics = analytics;
    }

    @GetMapping("/dashboard")
    public ApiResponse<AnalyticsDashboard> dashboard(
            @RequestParam(defaultValue = "daily") String period) {
        return ApiResponse.of(analytics.dashboard(period));
    }

    @GetMapping("/visitors")
    public ApiResponse<Map<String, Object>> visitors(
            @RequestParam(defaultValue = "monthly") String period) {
        AnalyticsDashboard result = analytics.dashboard(period);
        return ApiResponse.of(Map.of(
                "period", result.period(),
                "totalVisitors", result.totalVisitors(),
                "visitorsInPeriod", result.visitorsInPeriod()));
    }

    @GetMapping("/artifacts")
    public ApiResponse<java.util.List<com.smartheritage.backend.heritage.dto.ArtifactPopularity>> artifacts(
            @RequestParam(defaultValue = "monthly") String period) {
        return ApiResponse.of(analytics.dashboard(period).popularArtifacts());
    }

    @GetMapping("/interactions")
    public ApiResponse<Map<String, Object>> interactions(
            @RequestParam(defaultValue = "monthly") String period) {
        AnalyticsDashboard result = analytics.dashboard(period);
        return ApiResponse.of(Map.of(
                "period", result.period(),
                "interactions", result.interactionsInPeriod(),
                "averageEngagementSeconds", result.averageEngagementSeconds()));
    }

    @GetMapping("/languages")
    public ApiResponse<Map<String, Long>> languages(
            @RequestParam(defaultValue = "monthly") String period) {
        return ApiResponse.of(analytics.dashboard(period).languageDistribution());
    }
}
