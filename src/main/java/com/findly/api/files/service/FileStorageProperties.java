package com.findly.api.files.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "upload")
public record FileStorageProperties(
        String localDir,
        String publicPath
) {
}
