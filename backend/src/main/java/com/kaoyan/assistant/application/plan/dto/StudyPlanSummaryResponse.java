package com.kaoyan.assistant.application.plan.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudyPlanSummaryResponse(
        Long id,
        String planName,
        String planDescription,
        LocalDate startDate,
        LocalDate endDate,
        Long totalTaskCount,
        Long completedTaskCount,
        Integer completionRate,
        LocalDateTime createdAt
) {
}
