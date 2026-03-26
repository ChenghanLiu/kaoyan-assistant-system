package com.kaoyan.assistant.application.material.dto;

import org.springframework.core.io.Resource;

public record MaterialDownloadResponse(
        Resource resource,
        String fileName
) {
}
