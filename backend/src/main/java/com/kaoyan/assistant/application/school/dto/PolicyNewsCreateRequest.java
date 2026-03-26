package com.kaoyan.assistant.application.school.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PolicyNewsCreateRequest(
        Long schoolId,
        Long majorId,
        @NotBlank(message = "title is required")
        @Size(max = 200, message = "title length must be less than or equal to 200")
        String title,
        @NotBlank(message = "content is required")
        String content,
        @Size(max = 128, message = "sourceName length must be less than or equal to 128")
        String sourceName,
        @NotNull(message = "publishedDate is required")
        LocalDate publishedDate
) {
}
