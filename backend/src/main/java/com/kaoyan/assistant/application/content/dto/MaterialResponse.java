package com.kaoyan.assistant.application.content.dto;

import java.time.LocalDateTime;

public record MaterialResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String title,
        String description,
        String fileName,
        String filePath,
        Long fileSize,
        Integer downloadCount,
        String reviewStatus,
        String reviewComment,
        Long reviewerId,
        Long userId,
        String username,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt
) {
}
