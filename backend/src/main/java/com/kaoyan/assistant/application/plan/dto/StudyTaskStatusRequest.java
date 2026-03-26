package com.kaoyan.assistant.application.plan.dto;

import com.kaoyan.assistant.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record StudyTaskStatusRequest(
        @NotNull(message = "status is required")
        TaskStatus status
) {
}
