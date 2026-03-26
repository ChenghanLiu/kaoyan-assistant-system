package com.kaoyan.assistant.application.school.dto;

public record AdmissionGuideResponse(
        Long id,
        Long schoolId,
        String schoolName,
        Long majorId,
        String majorName,
        String title,
        String content,
        Integer guideYear
) {
}
