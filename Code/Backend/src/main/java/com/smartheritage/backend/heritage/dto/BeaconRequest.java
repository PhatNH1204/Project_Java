package com.smartheritage.backend.heritage.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record BeaconRequest(
        @NotBlank @Pattern(regexp = "(?i)^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
        String beaconUuid,
        @Min(0) @Max(65535) int major,
        @Min(0) @Max(65535) int minor,
        Long artifactId,
        @Size(max = 255) String placement,
        @Min(-90) @Max(90) BigDecimal latitude,
        @Min(-180) @Max(180) BigDecimal longitude,
        @Min(-127) @Max(20) int txPower,
        boolean active) {
}
