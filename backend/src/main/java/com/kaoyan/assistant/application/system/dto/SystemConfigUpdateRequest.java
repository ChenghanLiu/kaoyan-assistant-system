package com.kaoyan.assistant.application.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SystemConfigUpdateRequest(
        @NotBlank(message = "configValue is required")
        String configValue,
        @Size(max = 255, message = "configDescription must not exceed 255 characters")
        String configDescription
) {
}
