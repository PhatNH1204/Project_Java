package com.smartheritage.backend.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContentRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 10000) String description,
        @Size(max = 1000) String imageUrl,
        @Size(max = 1000) String videoUrl,
        @Size(max = 1000) String audioUrl) {
}
