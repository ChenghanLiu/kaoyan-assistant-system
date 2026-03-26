package com.kaoyan.assistant.application.school.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PolicyNewsUpdateRequest(
        Long schoolId,
        Long majorId,
        @Size(max = 200, message = "title length must be less than or equal to 200")
        String title,
        String content,
        @Size(max = 128, message = "sourceName length must be less than or equal to 128")
        String sourceName,
        LocalDate publishedDate
) {
}
