package com.kaoyan.assistant.application.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateRequest(
        @NotBlank(message = "title is required")
        @Size(max = 200, message = "title length must be less than or equal to 200")
        String title,
        @NotBlank(message = "content is required")
        String content
) {
}
