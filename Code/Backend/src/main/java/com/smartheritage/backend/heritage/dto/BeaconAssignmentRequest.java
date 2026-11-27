package com.smartheritage.backend.heritage.dto;

import jakarta.validation.constraints.NotNull;

public record BeaconAssignmentRequest(@NotNull Long artifactId) {
}
