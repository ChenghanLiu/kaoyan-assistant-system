package com.kaoyan.assistant.application.school.dto;

public record ApplicationRatioResponse(
        Long id,
        Long schoolId,
        String schoolName,
        Long majorId,
        String majorName,
        Integer ratioYear,
        Integer applyCount,
        Integer admitCount,
        String ratioValue
) {
}
