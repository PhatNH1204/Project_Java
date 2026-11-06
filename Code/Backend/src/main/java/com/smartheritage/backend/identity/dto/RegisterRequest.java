package com.smartheritage.backend.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._-]{3,30}$")
        String username,
        @NotBlank
        @Email
        @Size(max = 254)
        String email,
        @NotBlank
        @Size(min = 8, max = 72)
        String password,
        @NotBlank
        @Size(max = 10)
        @Pattern(regexp = "^[a-zA-Z]{2,10}$")
        String languageCode) {
}
