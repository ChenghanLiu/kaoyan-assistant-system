package com.kaoyan.assistant.application.exam.dto;

public record SubmitExamResponse(
        Long recordId,
        Integer score,
        Integer totalScore,
        Integer correctCount,
        Integer totalCount
) {
}
