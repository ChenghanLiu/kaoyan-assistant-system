package com.kaoyan.assistant.application.plan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateStudyPlanRequest(
        @NotBlank(message = "planName is required")
        @Size(max = 128, message = "planName must not exceed 128 characters")
        String planName,
        @Size(max = 2000, message = "planDescription must not exceed 2000 characters")
        String planDescription,
        LocalDate startDate,
        LocalDate endDate
) {
}
