package com.kaoyan.assistant.application.exam.dto;

public record ExamOptionResponse(
        Long id,
        String optionLabel,
        String optionContent
) {
}
