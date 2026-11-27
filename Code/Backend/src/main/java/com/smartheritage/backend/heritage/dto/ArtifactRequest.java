package com.smartheritage.backend.heritage.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ArtifactRequest(
        long siteId,
        @NotBlank @Size(max = 50) @Pattern(regexp = "^[a-zA-Z0-9._-]+$") String code,
        @Size(max = 160) String locationName,
        @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
        boolean active,
        @NotBlank @Pattern(regexp = "^[a-zA-Z]{2,10}$") String languageCode,
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 10000) String description,
        @Size(max = 1000) String imageUrl,
        @Size(max = 1000) String videoUrl,
        @Size(max = 1000) String audioUrl) {
}
