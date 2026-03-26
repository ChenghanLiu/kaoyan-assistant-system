package com.kaoyan.assistant.application.school.dto;

import jakarta.validation.constraints.Size;

public record AdmissionGuideUpdateRequest(
        Long schoolId,
        Long majorId,
        @Size(max = 200, message = "title length must be less than or equal to 200")
        String title,
        String content,
        Integer guideYear
) {
}
