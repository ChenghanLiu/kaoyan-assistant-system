package com.kaoyan.assistant.application.school.dto;

public record MajorResponse(
        Long id,
        Long schoolId,
        String majorName,
        String majorCode,
        String degreeType,
        String description
) {
}
