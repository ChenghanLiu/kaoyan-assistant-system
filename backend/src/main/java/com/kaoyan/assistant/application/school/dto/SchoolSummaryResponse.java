package com.kaoyan.assistant.application.school.dto;

public record SchoolSummaryResponse(
        Long id,
        String schoolName,
        String province,
        String city,
        String schoolType,
        String schoolLevel,
        String description
) {
}
