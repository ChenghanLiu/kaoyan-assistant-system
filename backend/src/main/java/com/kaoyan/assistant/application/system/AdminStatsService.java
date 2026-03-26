package com.kaoyan.assistant.application.system;

import com.kaoyan.assistant.application.system.dto.AdminStatsOverviewResponse;
import com.kaoyan.assistant.domain.enums.MaterialStatus;
import com.kaoyan.assistant.domain.enums.RoleCode;
import com.kaoyan.assistant.domain.enums.TaskStatus;
import com.kaoyan.assistant.infrastructure.repository.ExamRecordRepository;
import com.kaoyan.assistant.infrastructure.repository.MaterialRepository;
import com.kaoyan.assistant.infrastructure.repository.PostCommentRepository;
import com.kaoyan.assistant.infrastructure.repository.PostRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyPlanRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyTaskRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AdminStatsService {

    private final SysUserRepository sysUserRepository;
    private final StudyPlanRepository studyPlanRepository;
    private final StudyTaskRepository studyTaskRepository;
    private final ExamRecordRepository examRecordRepository;
    private final MaterialRepository materialRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    public AdminStatsService(SysUserRepository sysUserRepository,
                             StudyPlanRepository studyPlanRepository,
                             StudyTaskRepository studyTaskRepository,
                             ExamRecordRepository examRecordRepository,
                             MaterialRepository materialRepository,
                             PostRepository postRepository,
                             PostCommentRepository postCommentRepository) {
        this.sysUserRepository = sysUserRepository;
        this.studyPlanRepository = studyPlanRepository;
        this.studyTaskRepository = studyTaskRepository;
        this.examRecordRepository = examRecordRepository;
        this.materialRepository = materialRepository;
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
    }

    @Transactional(readOnly = true)
    public AdminStatsOverviewResponse getOverview() {
        long totalUsers = sysUserRepository.count();
        long studentCount = sysUserRepository.countDistinctByRoleCode(RoleCode.STUDENT);
        long adminCount = sysUserRepository.countDistinctByRoleCode(RoleCode.ADMIN);

        long totalPlans = studyPlanRepository.count();
        long completedTaskCount = studyTaskRepository.countByStatus(TaskStatus.DONE);
        long totalTaskCount = studyTaskRepository.count();
        long pendingTaskCount = totalTaskCount - completedTaskCount;
        double averageCompletionRate = totalTaskCount == 0 ? 0D : round((double) completedTaskCount * 100D / totalTaskCount);

        long participationCount = examRecordRepository.count();
        double averageScore = round(examRecordRepository.findAverageScore());
        int highestScore = examRecordRepository.findHighestScore();
        int lowestScore = examRecordRepository.findLowestScore();

        long totalMaterials = materialRepository.count();
        long approvedCount = materialRepository.countByReviewStatus(MaterialStatus.APPROVED);
        long pendingCount = materialRepository.countByReviewStatus(MaterialStatus.PENDING);

        return new AdminStatsOverviewResponse(
                new AdminStatsOverviewResponse.UserStats(totalUsers, studentCount, adminCount),
                new AdminStatsOverviewResponse.StudyPlanStats(totalPlans, completedTaskCount, pendingTaskCount, averageCompletionRate),
                new AdminStatsOverviewResponse.ExamStats(participationCount, averageScore, highestScore, lowestScore),
                new AdminStatsOverviewResponse.MaterialStats(totalMaterials, approvedCount, pendingCount),
                new AdminStatsOverviewResponse.ContentStats(postRepository.count(), postCommentRepository.count())
        );
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
