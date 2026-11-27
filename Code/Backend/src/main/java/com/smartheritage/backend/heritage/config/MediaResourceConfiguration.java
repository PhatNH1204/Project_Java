package com.smartheritage.backend.heritage.config;

import com.smartheritage.backend.heritage.service.MediaStorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MediaResourceConfiguration implements WebMvcConfigurer {

    private final MediaStorageService storage;

    public MediaResourceConfiguration(MediaStorageService storage) {
        this.storage = storage;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/media/**")
                .addResourceLocations(storage.location());
    }
}
