package com.smartheritage.backend.heritage.dto;

import java.math.BigDecimal;

public record ArtifactView(
        Long id,
        String code,
        Long siteId,
        String siteName,
        String locationName,
        BigDecimal latitude,
        BigDecimal longitude,
        ContentView content,
        boolean active) {
}
