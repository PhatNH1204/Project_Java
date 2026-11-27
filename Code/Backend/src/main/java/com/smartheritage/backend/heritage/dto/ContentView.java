package com.smartheritage.backend.heritage.dto;

public record ContentView(
        String languageCode,
        String title,
        String description,
        String imageUrl,
        String videoUrl,
        String audioUrl) {
}
