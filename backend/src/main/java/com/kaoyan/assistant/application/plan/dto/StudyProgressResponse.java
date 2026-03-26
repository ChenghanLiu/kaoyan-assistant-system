package com.kaoyan.assistant.application.plan.dto;

import java.time.LocalDateTime;

public record StudyProgressResponse(
        Long id,
        Long taskId,
        String progressNote,
        Integer progressPercent,
        Integer studyMinutes,
        LocalDateTime recordedAt
) {
}
