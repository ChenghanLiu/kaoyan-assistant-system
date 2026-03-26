package com.kaoyan.assistant.application.content.dto;

import jakarta.validation.constraints.NotBlank;

public record PostCommentCreateRequest(
        @NotBlank(message = "content is required")
        String content
) {
}
