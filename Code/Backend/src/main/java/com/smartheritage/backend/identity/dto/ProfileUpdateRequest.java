package com.smartheritage.backend.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank @Pattern(regexp = "^[a-zA-Z0-9._-]{3,30}$") String username,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Pattern(regexp = "^[a-zA-Z]{2,10}$") String preferredLanguage) {
}
