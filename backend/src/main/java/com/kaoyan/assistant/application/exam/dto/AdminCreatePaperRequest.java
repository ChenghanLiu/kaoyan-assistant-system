package com.kaoyan.assistant.application.exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminCreatePaperRequest(
        @NotBlank(message = "title is required")
        String title,
        String description,
        @NotNull(message = "paperYear is required")
        Integer paperYear
) {
}
