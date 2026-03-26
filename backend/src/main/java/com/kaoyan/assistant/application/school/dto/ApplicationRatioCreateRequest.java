package com.kaoyan.assistant.application.school.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplicationRatioCreateRequest(
        @NotNull(message = "schoolId is required")
        Long schoolId,
        @NotNull(message = "majorId is required")
        Long majorId,
        @NotNull(message = "ratioYear is required")
        Integer ratioYear,
        @NotNull(message = "applyCount is required")
        Integer applyCount,
        @NotNull(message = "admitCount is required")
        Integer admitCount,
        @NotBlank(message = "ratioValue is required")
        @Size(max = 32, message = "ratioValue length must be less than or equal to 32")
        String ratioValue
) {
}
