package com.smartheritage.backend.heritage.dto;

import java.math.BigDecimal;

public record SiteView(
        Long id,
        String name,
        String description,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        boolean active) {
}
