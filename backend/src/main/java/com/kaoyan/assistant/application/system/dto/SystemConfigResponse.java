package com.kaoyan.assistant.application.system.dto;

import java.time.LocalDateTime;

public record SystemConfigResponse(
        Long id,
        String configKey,
        String configValue,
        String configDescription,
        LocalDateTime updatedAt
) {
}
