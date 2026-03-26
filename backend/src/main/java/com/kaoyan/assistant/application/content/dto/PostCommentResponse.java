package com.kaoyan.assistant.application.content.dto;

import java.time.LocalDateTime;

public record PostCommentResponse(
        Long id,
        Long postId,
        Long userId,
        String username,
        String displayName,
        String content,
        LocalDateTime createdAt
) {
}
