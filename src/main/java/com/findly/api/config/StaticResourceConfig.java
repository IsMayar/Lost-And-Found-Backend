package com.findly.api.config;

import com.findly.api.files.service.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(FileStorageProperties.class)
public class StaticResourceConfig implements WebMvcConfigurer {

    private final FileStorageProperties fileStorageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String publicPath = normalizePublicPath(fileStorageProperties.publicPath());
        Path uploadDir = Paths.get(fileStorageProperties.localDir()).toAbsolutePath().normalize();

        registry.addResourceHandler(publicPath + "/**")
                .addResourceLocations(uploadDir.toUri().toString());
    }

    private String normalizePublicPath(String publicPath) {
        if (publicPath == null || publicPath.isBlank()) {
            return "/uploads";
        }

        String normalized = publicPath.trim();

        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }

        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }
}
