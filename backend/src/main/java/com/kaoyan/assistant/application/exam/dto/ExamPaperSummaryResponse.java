package com.kaoyan.assistant.application.exam.dto;

import java.time.LocalDateTime;

public record ExamPaperSummaryResponse(
        Long id,
        String title,
        String description,
        Integer paperYear,
        Integer questionCount,
        Integer totalScore,
        LocalDateTime createdAt
) {
}
