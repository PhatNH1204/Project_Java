package com.smartheritage.backend.identity.dto;

public record UserProfileResponse(
        Long userId,
        String username,
        String email,
        String role,
        String preferredLanguage,
        boolean active) {
}
