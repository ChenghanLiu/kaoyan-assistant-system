package com.kaoyan.assistant.application.system.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SystemNoticeUpdateRequest(
        @Size(max = 200, message = "title must not exceed 200 characters")
        String title,
        String content,
        @Pattern(regexp = "ALL|STUDENT|ADMIN", message = "targetRole must be ALL, STUDENT or ADMIN")
        String targetRole
) {
}
