package com.smartheritage.backend.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FeedbackModerationRequest(
        @NotBlank @Pattern(regexp = "^(APPROVED|REJECTED)$") String status) {
}
