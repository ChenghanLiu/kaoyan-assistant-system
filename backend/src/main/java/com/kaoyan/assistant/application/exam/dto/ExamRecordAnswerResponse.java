package com.kaoyan.assistant.application.exam.dto;

import java.util.List;

public record ExamRecordAnswerResponse(
        Long questionId,
        String questionType,
        String questionStem,
        Integer score,
        Integer awardedScore,
        boolean correct,
        List<String> selectedOptions,
        List<String> correctOptions,
        String analysisText,
        List<ExamOptionResponse> options
) {
}
