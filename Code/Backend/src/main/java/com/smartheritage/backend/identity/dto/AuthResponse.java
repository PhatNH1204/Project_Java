package com.smartheritage.backend.identity.dto;

public record AuthResponse(
        Long userId,
        String username,
        String email,
        String role,
        String preferredLanguage) {
}
