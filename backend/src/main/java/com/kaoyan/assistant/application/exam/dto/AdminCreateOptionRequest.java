package com.kaoyan.assistant.application.exam.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminCreateOptionRequest(
        @NotBlank(message = "optionLabel is required")
        String optionLabel,
        @NotBlank(message = "optionContent is required")
        String optionContent
) {
}
