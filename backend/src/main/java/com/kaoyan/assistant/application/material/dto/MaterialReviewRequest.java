package com.kaoyan.assistant.application.material.dto;

import com.kaoyan.assistant.domain.enums.MaterialStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MaterialReviewRequest(
        @NotNull(message = "reviewStatus is required")
        MaterialStatus reviewStatus,
        @Size(max = 500, message = "reviewComment must not exceed 500 characters")
        String reviewComment
) {
}
