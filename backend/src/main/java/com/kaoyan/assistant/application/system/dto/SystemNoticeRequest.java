package com.kaoyan.assistant.application.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SystemNoticeRequest(
        @NotBlank(message = "title is required")
        @Size(max = 200, message = "title must not exceed 200 characters")
        String title,
        @NotBlank(message = "content is required")
        String content,
        @NotBlank(message = "targetRole is required")
        @Pattern(regexp = "ALL|STUDENT|ADMIN", message = "targetRole must be ALL, STUDENT or ADMIN")
        String targetRole
) {
}
