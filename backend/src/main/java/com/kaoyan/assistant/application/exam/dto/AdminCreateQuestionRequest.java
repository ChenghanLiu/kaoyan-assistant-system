package com.kaoyan.assistant.application.exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminCreateQuestionRequest(
        @NotNull(message = "paperId is required")
        Long paperId,
        @NotBlank(message = "questionType is required")
        String questionType,
        @NotBlank(message = "questionStem is required")
        String questionStem,
        @NotBlank(message = "correctAnswer is required")
        String correctAnswer,
        @NotNull(message = "score is required")
        Integer score,
        String analysisText,
        Integer sortOrder
) {
}
