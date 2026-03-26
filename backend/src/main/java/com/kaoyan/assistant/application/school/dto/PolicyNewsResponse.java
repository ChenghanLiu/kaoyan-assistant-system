package com.kaoyan.assistant.application.school.dto;

import java.time.LocalDate;

public record PolicyNewsResponse(
        Long id,
        Long schoolId,
        String schoolName,
        Long majorId,
        String majorName,
        String title,
        String content,
        String sourceName,
        LocalDate publishedDate
) {
}
