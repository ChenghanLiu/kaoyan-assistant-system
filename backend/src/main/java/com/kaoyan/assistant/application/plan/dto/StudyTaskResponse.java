package com.kaoyan.assistant.application.plan.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record StudyTaskResponse(
        Long id,
        Long planId,
        String taskName,
        String taskDescription,
        LocalDate dueDate,
        String status,
        LocalDateTime completedAt,
        Integer latestProgressPercent,
        List<StudyProgressResponse> progressRecords,
        LocalDateTime createdAt
) {
}
