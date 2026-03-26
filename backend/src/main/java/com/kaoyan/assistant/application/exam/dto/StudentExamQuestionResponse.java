package com.kaoyan.assistant.application.exam.dto;

import java.util.List;

public record StudentExamQuestionResponse(
        Long id,
        String questionType,
        String questionStem,
        Integer score,
        Integer sortOrder,
        List<ExamOptionResponse> options
) {
}
