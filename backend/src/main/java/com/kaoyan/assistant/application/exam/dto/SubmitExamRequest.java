package com.kaoyan.assistant.application.exam.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubmitExamRequest(
        @NotNull(message = "paperId is required")
        Long paperId,
        @Valid
        @NotEmpty(message = "answers is required")
        List<SubmitAnswerRequest> answers
) {

    public record SubmitAnswerRequest(
            @NotNull(message = "questionId is required")
            Long questionId,
            List<String> selectedOptions
    ) {
    }
}
