package com.kaoyan.assistant.application.system.dto;

public record AdminStatsOverviewResponse(
        UserStats userStats,
        StudyPlanStats studyPlanStats,
        ExamStats examStats,
        MaterialStats materialStats,
        ContentStats contentStats
) {

    public record UserStats(
            long totalUsers,
            long studentCount,
            long adminCount
    ) {
    }

    public record StudyPlanStats(
            long totalPlans,
            long completedTaskCount,
            long pendingTaskCount,
            double averageCompletionRate
    ) {
    }

    public record ExamStats(
            long participationCount,
            double averageScore,
            int highestScore,
            int lowestScore
    ) {
    }

    public record MaterialStats(
            long totalMaterials,
            long approvedCount,
            long pendingCount
    ) {
    }

    public record ContentStats(
            long postCount,
            long commentCount
    ) {
    }
}
