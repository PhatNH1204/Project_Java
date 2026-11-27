package com.smartheritage.backend.heritage.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InteractionRequest(
        @NotBlank @Pattern(regexp = "^[0-9a-fA-F-]{36}$") String eventKey,
        @NotNull Long artifactId,
        Long beaconId,
        @NotBlank @Pattern(regexp = "^[a-zA-Z]{2,10}$") String languageCode,
        @Min(0) @jakarta.validation.constraints.Max(86400) int durationSeconds) {
}
