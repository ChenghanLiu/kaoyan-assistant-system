package com.kaoyan.assistant.application.exam.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ExamRecordDetailResponse(
        Long id,
        Long paperId,
        String paperTitle,
        Integer score,
        Integer totalScore,
        Integer correctCount,
        Integer totalCount,
        LocalDateTime submittedAt,
        List<ExamRecordAnswerResponse> answers
) {
}
