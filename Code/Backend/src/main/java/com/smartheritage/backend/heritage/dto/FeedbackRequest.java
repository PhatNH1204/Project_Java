package com.smartheritage.backend.heritage.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(
        @NotNull Long artifactId,
        @Min(1) @Max(5) int rating,
        @Size(max = 2000) String comment) {
}
