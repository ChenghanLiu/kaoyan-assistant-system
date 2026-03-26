package com.kaoyan.assistant.application.exam.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WrongQuestionResponse(
        Long wrongQuestionId,
        Long paperId,
        String paperTitle,
        Long questionId,
        String questionType,
        String questionStem,
        Integer score,
        List<String> latestAnswer,
        List<String> correctAnswer,
        String analysisText,
        List<ExamOptionResponse> options,
        LocalDateTime updatedAt
) {
}
