package com.kaoyan.assistant.application.system.dto;

import java.time.LocalDateTime;

public record OperationLogResponse(
        Long id,
        Long userId,
        String username,
        String userRole,
        String operationModule,
        String operationType,
        String operationContent,
        String requestPath,
        LocalDateTime createdAt
) {
}
