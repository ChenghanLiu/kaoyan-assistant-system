package com.kaoyan.assistant.application.system.dto;

import java.time.LocalDateTime;

public record SystemNoticeResponse(
        Long id,
        String title,
        String content,
        String targetRole,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
