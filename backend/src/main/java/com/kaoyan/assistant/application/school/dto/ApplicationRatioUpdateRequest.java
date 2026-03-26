package com.kaoyan.assistant.application.school.dto;

import jakarta.validation.constraints.Size;

public record ApplicationRatioUpdateRequest(
        Long schoolId,
        Long majorId,
        Integer ratioYear,
        Integer applyCount,
        Integer admitCount,
        @Size(max = 32, message = "ratioValue length must be less than or equal to 32")
        String ratioValue
) {
}
