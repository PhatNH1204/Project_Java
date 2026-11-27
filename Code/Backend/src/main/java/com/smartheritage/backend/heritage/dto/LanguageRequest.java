package com.smartheritage.backend.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LanguageRequest(
        @NotBlank @Pattern(regexp = "^[a-zA-Z]{2,10}$") String code,
        @NotBlank @Size(max = 80) String name) {
}
