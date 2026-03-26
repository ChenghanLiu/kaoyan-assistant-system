package com.kaoyan.assistant.application.content.dto;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        Integer viewCount,
        Long userId,
        String username,
        String displayName,
        LocalDateTime createdAt
) {
}
