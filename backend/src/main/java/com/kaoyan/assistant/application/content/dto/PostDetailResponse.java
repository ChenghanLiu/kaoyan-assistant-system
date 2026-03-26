package com.kaoyan.assistant.application.content.dto;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        Integer viewCount,
        Long userId,
        String username,
        String displayName,
        java.time.LocalDateTime createdAt
) {
}
