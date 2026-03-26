package com.kaoyan.assistant.application.plan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateStudyTaskRequest(
        @NotBlank(message = "taskName is required")
        @Size(max = 128, message = "taskName must not exceed 128 characters")
        String taskName,
        @Size(max = 2000, message = "taskDescription must not exceed 2000 characters")
        String taskDescription,
        LocalDate dueDate
) {
}
