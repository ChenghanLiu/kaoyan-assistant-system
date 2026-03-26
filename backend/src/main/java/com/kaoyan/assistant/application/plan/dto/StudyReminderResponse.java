package com.kaoyan.assistant.application.plan.dto;

import java.time.LocalDateTime;

public record StudyReminderResponse(
        Long id,
        Long planId,
        Long taskId,
        String title,
        String content,
        String reminderType,
        Boolean isRead,
        LocalDateTime remindAt,
        LocalDateTime createdAt
) {
}
