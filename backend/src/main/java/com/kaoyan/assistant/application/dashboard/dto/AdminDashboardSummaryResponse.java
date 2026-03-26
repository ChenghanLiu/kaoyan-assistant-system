package com.kaoyan.assistant.application.dashboard.dto;

public record AdminDashboardSummaryResponse(
        long userCount,
        long schoolCount,
        long majorCount,
        long materialCount,
        long pendingMaterialCount,
        long postCount,
        long examPaperCount
) {
}
