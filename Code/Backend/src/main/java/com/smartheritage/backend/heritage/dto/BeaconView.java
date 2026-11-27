package com.smartheritage.backend.heritage.dto;

import java.math.BigDecimal;

public record BeaconView(
        Long id,
        String beaconUuid,
        int major,
        int minor,
        Long artifactId,
        String artifactCode,
        String placement,
        BigDecimal latitude,
        BigDecimal longitude,
        int txPower,
        boolean active) {
}
