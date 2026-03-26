package com.kaoyan.assistant.application.plan.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStudyProgressRequest(
        @Size(max = 2000, message = "progressNote must not exceed 2000 characters")
        String progressNote,
        @NotNull(message = "progressPercent is required")
        @Min(value = 0, message = "progressPercent must be between 0 and 100")
        @Max(value = 100, message = "progressPercent must be between 0 and 100")
        Integer progressPercent,
        @NotNull(message = "studyMinutes is required")
        @Min(value = 0, message = "studyMinutes must be greater than or equal to 0")
        Integer studyMinutes
) {
}
