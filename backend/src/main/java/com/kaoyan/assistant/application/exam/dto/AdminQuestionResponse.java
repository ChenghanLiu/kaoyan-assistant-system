package com.kaoyan.assistant.application.exam.dto;

import java.time.LocalDateTime;

public record AdminQuestionResponse(
        Long id,
        Long paperId,
        String paperTitle,
        String questionType,
        String questionStem,
        String correctAnswer,
        Integer score,
        String analysisText,
        Integer sortOrder,
        LocalDateTime createdAt
) {
}
