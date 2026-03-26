package com.kaoyan.assistant.application.exam.dto;

import java.time.LocalDateTime;

public record ExamRecordSummaryResponse(
        Long id,
        Long paperId,
        String paperTitle,
        Integer score,
        Integer totalScore,
        Integer correctCount,
        Integer totalCount,
        LocalDateTime submittedAt
) {
}
