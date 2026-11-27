package com.smartheritage.backend.heritage.config;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdminWebConfiguration implements WebMvcConfigurer {

    private final String location;

    public AdminWebConfiguration(@Value("${app.admin-ui-dir}") String directory) {
        String uri = Path.of(directory).toAbsolutePath().normalize().toUri().toString();
        this.location = uri.endsWith("/") ? uri : uri + "/";
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/admin/**")
                .addResourceLocations(location);
    }
}
