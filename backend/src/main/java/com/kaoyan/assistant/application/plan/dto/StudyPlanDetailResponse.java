package com.kaoyan.assistant.application.plan.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record StudyPlanDetailResponse(
        Long id,
        String planName,
        String planDescription,
        LocalDate startDate,
        LocalDate endDate,
        Long totalTaskCount,
        Long completedTaskCount,
        Integer completionRate,
        List<StudyTaskResponse> tasks,
        List<StudyReminderResponse> reminders,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
