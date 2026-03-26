package com.kaoyan.assistant.application.plan;

import com.kaoyan.assistant.application.plan.dto.CreateStudyPlanRequest;
import com.kaoyan.assistant.application.plan.dto.CreateStudyProgressRequest;
import com.kaoyan.assistant.application.plan.dto.CreateStudyTaskRequest;
import com.kaoyan.assistant.application.plan.dto.StudyPlanDetailResponse;
import com.kaoyan.assistant.application.plan.dto.StudyPlanSummaryResponse;
import com.kaoyan.assistant.application.plan.dto.StudyProgressResponse;
import com.kaoyan.assistant.application.plan.dto.StudyReminderResponse;
import com.kaoyan.assistant.application.plan.dto.StudyTaskResponse;
import com.kaoyan.assistant.application.system.OperationLogService;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.domain.entity.StudyPlan;
import com.kaoyan.assistant.domain.entity.StudyProgress;
import com.kaoyan.assistant.domain.entity.StudyReminder;
import com.kaoyan.assistant.domain.entity.StudyTask;
import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.domain.enums.ReminderType;
import com.kaoyan.assistant.domain.enums.TaskStatus;
import com.kaoyan.assistant.infrastructure.repository.StudyPlanRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyProgressRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyReminderRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyTaskRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final StudyTaskRepository studyTaskRepository;
    private final StudyProgressRepository studyProgressRepository;
    private final StudyReminderRepository studyReminderRepository;
    private final SysUserRepository sysUserRepository;
    private final OperationLogService operationLogService;

    public StudyPlanService(StudyPlanRepository studyPlanRepository,
                            StudyTaskRepository studyTaskRepository,
                            StudyProgressRepository studyProgressRepository,
                            StudyReminderRepository studyReminderRepository,
                            SysUserRepository sysUserRepository,
                            OperationLogService operationLogService) {
        this.studyPlanRepository = studyPlanRepository;
        this.studyTaskRepository = studyTaskRepository;
        this.studyProgressRepository = studyProgressRepository;
        this.studyReminderRepository = studyReminderRepository;
        this.sysUserRepository = sysUserRepository;
        this.operationLogService = operationLogService;
    }

    @Transactional
    public StudyPlanSummaryResponse createPlan(Long userId, CreateStudyPlanRequest request) {
        validatePlanDateRange(request.startDate(), request.endDate());

        StudyPlan plan = new StudyPlan();
        plan.setUserId(userId);
        plan.setPlanName(request.planName().trim());
        plan.setPlanDescription(normalizeText(request.planDescription()));
        plan.setStartDate(request.startDate());
        plan.setEndDate(request.endDate());
        StudyPlan savedPlan = studyPlanRepository.save(plan);

        createReminder(userId, savedPlan.getId(), null, "学习计划已创建",
                "计划《" + savedPlan.getPlanName() + "》已创建，请开始拆解任务。", ReminderType.SYSTEM, LocalDateTime.now());
        operationLogService.record(toLoginUser(userId), "PLAN", "CREATE",
                "创建学习计划《" + savedPlan.getPlanName() + "》", "/api/student/plans");
        return toPlanSummary(savedPlan);
    }

    @Transactional(readOnly = true)
    public List<StudyPlanSummaryResponse> listPlans(Long userId) {
        return studyPlanRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toPlanSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudyPlanDetailResponse getPlanDetail(Long userId, Long planId) {
        StudyPlan plan = requireOwnedPlan(userId, planId);
        List<StudyTask> tasks = studyTaskRepository.findByPlanIdAndUserIdOrderByDueDateAscCreatedAtAsc(planId, userId);
        Map<Long, List<StudyProgress>> progressMap = studyProgressRepository.findByPlanIdAndUserIdOrderByRecordedAtDesc(planId, userId)
                .stream()
                .collect(Collectors.groupingBy(StudyProgress::getTaskId));
        List<StudyReminderResponse> reminders = studyReminderRepository.findByUserIdAndPlanIdOrderByRemindAtDescCreatedAtDesc(userId, planId)
                .stream()
                .map(this::toReminderResponse)
                .toList();

        long totalTaskCount = tasks.size();
        long completedTaskCount = tasks.stream().filter(task -> task.getStatus() == TaskStatus.DONE).count();

        return new StudyPlanDetailResponse(
                plan.getId(),
                plan.getPlanName(),
                plan.getPlanDescription(),
                plan.getStartDate(),
                plan.getEndDate(),
                totalTaskCount,
                completedTaskCount,
                calculateCompletionRate(totalTaskCount, completedTaskCount),
                tasks.stream().map(task -> toTaskResponse(task, progressMap.getOrDefault(task.getId(), List.of()))).toList(),
                reminders,
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }

    @Transactional
    public StudyTaskResponse createTask(Long userId, Long planId, CreateStudyTaskRequest request) {
        StudyPlan plan = requireOwnedPlan(userId, planId);

        StudyTask task = new StudyTask();
        task.setPlanId(plan.getId());
        task.setUserId(userId);
        task.setTaskName(request.taskName().trim());
        task.setTaskDescription(normalizeText(request.taskDescription()));
        task.setDueDate(request.dueDate());
        task.setStatus(TaskStatus.TODO);
        StudyTask savedTask = studyTaskRepository.save(task);

        createReminder(userId, plan.getId(), savedTask.getId(), "新学习任务",
                "任务《" + savedTask.getTaskName() + "》已加入计划。", ReminderType.SYSTEM, LocalDateTime.now());
        if (savedTask.getDueDate() != null) {
            createReminder(userId, plan.getId(), savedTask.getId(), "任务到期提醒",
                    "任务《" + savedTask.getTaskName() + "》截止日期为 " + savedTask.getDueDate() + "。",
                    ReminderType.DUE,
                    savedTask.getDueDate().atTime(LocalTime.of(9, 0)));
        }
        operationLogService.record(toLoginUser(userId), "PLAN", "CREATE_TASK",
                "创建学习任务《" + savedTask.getTaskName() + "》", "/api/student/plans/" + planId + "/tasks");

        return toTaskResponse(savedTask, List.of());
    }

    @Transactional(readOnly = true)
    public List<StudyTaskResponse> listPlanTasks(Long userId, Long planId) {
        requireOwnedPlan(userId, planId);
        return studyTaskRepository.findByPlanIdAndUserIdOrderByDueDateAscCreatedAtAsc(planId, userId)
                .stream()
                .map(task -> toTaskResponse(task, studyProgressRepository.findByTaskIdAndUserIdOrderByRecordedAtDesc(task.getId(), userId)))
                .toList();
    }

    @Transactional
    public StudyTaskResponse updateTaskStatus(Long userId, Long taskId, TaskStatus status) {
        StudyTask task = requireOwnedTask(userId, taskId);
        if (status != TaskStatus.DONE) {
            task.setCompletedAt(null);
        }
        task.setStatus(status);
        if (status == TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        }
        StudyTask savedTask = studyTaskRepository.save(task);

        createReminder(userId, task.getPlanId(), task.getId(), "任务状态更新",
                "任务《" + task.getTaskName() + "》状态已更新为 " + status + "。", ReminderType.SYSTEM, LocalDateTime.now());
        return toTaskResponse(savedTask, studyProgressRepository.findByTaskIdAndUserIdOrderByRecordedAtDesc(taskId, userId));
    }

    @Transactional
    public StudyProgressResponse recordProgress(Long userId, Long taskId, CreateStudyProgressRequest request) {
        StudyTask task = requireOwnedTask(userId, taskId);

        StudyProgress progress = new StudyProgress();
        progress.setPlanId(task.getPlanId());
        progress.setTaskId(task.getId());
        progress.setUserId(userId);
        progress.setProgressNote(normalizeText(request.progressNote()));
        progress.setProgressPercent(request.progressPercent());
        progress.setStudyMinutes(request.studyMinutes());
        progress.setRecordedAt(LocalDateTime.now());
        StudyProgress savedProgress = studyProgressRepository.save(progress);

        if (request.progressPercent() >= 100) {
            task.setStatus(TaskStatus.DONE);
            task.setCompletedAt(LocalDateTime.now());
        } else if (task.getStatus() == TaskStatus.TODO && request.progressPercent() > 0) {
            task.setStatus(TaskStatus.IN_PROGRESS);
            task.setCompletedAt(null);
        }
        studyTaskRepository.save(task);

        createReminder(userId, task.getPlanId(), task.getId(), "学习进度已记录",
                "任务《" + task.getTaskName() + "》新增一条学习进度记录。", ReminderType.SYSTEM, LocalDateTime.now());
        return toProgressResponse(savedProgress);
    }

    @Transactional(readOnly = true)
    public List<StudyReminderResponse> listReminders(Long userId) {
        return studyReminderRepository.findByUserIdOrderByRemindAtDescCreatedAtDesc(userId)
                .stream()
                .map(this::toReminderResponse)
                .toList();
    }

    private StudyPlan requireOwnedPlan(Long userId, Long planId) {
        StudyPlan plan = studyPlanRepository.findById(planId)
                .orElseThrow(() -> BusinessException.notFound("study plan not found"));
        if (!plan.getUserId().equals(userId)) {
            throw BusinessException.forbidden("forbidden");
        }
        return plan;
    }

    private StudyTask requireOwnedTask(Long userId, Long taskId) {
        StudyTask task = studyTaskRepository.findById(taskId)
                .orElseThrow(() -> BusinessException.notFound("study task not found"));
        if (!task.getUserId().equals(userId)) {
            throw BusinessException.forbidden("forbidden");
        }
        return task;
    }

    private void validatePlanDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw BusinessException.invalidInput("endDate must be greater than or equal to startDate");
        }
    }

    private void createReminder(Long userId, Long planId, Long taskId, String title, String content,
                                ReminderType reminderType, LocalDateTime remindAt) {
        StudyReminder reminder = new StudyReminder();
        reminder.setUserId(userId);
        reminder.setPlanId(planId);
        reminder.setTaskId(taskId);
        reminder.setTitle(title);
        reminder.setContent(content);
        reminder.setReminderType(reminderType);
        reminder.setIsRead(false);
        reminder.setRemindAt(remindAt);
        studyReminderRepository.save(reminder);
    }

    private LoginUser toLoginUser(Long userId) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("user not found"));
        return new LoginUser(user);
    }

    private StudyPlanSummaryResponse toPlanSummary(StudyPlan plan) {
        long totalTaskCount = studyTaskRepository.countByPlanIdAndUserId(plan.getId(), plan.getUserId());
        long completedTaskCount = studyTaskRepository.countByPlanIdAndUserIdAndStatus(plan.getId(), plan.getUserId(), TaskStatus.DONE);
        return new StudyPlanSummaryResponse(
                plan.getId(),
                plan.getPlanName(),
                plan.getPlanDescription(),
                plan.getStartDate(),
                plan.getEndDate(),
                totalTaskCount,
                completedTaskCount,
                calculateCompletionRate(totalTaskCount, completedTaskCount),
                plan.getCreatedAt()
        );
    }

    private StudyTaskResponse toTaskResponse(StudyTask task, List<StudyProgress> progressRecords) {
        List<StudyProgressResponse> progressResponses = progressRecords.stream()
                .map(this::toProgressResponse)
                .toList();
        Integer latestProgressPercent = progressResponses.isEmpty() ? 0 : progressResponses.get(0).progressPercent();
        return new StudyTaskResponse(
                task.getId(),
                task.getPlanId(),
                task.getTaskName(),
                task.getTaskDescription(),
                task.getDueDate(),
                task.getStatus().name(),
                task.getCompletedAt(),
                latestProgressPercent,
                progressResponses,
                task.getCreatedAt()
        );
    }

    private StudyProgressResponse toProgressResponse(StudyProgress progress) {
        return new StudyProgressResponse(
                progress.getId(),
                progress.getTaskId(),
                progress.getProgressNote(),
                progress.getProgressPercent(),
                progress.getStudyMinutes(),
                progress.getRecordedAt()
        );
    }

    private StudyReminderResponse toReminderResponse(StudyReminder reminder) {
        return new StudyReminderResponse(
                reminder.getId(),
                reminder.getPlanId(),
                reminder.getTaskId(),
                reminder.getTitle(),
                reminder.getContent(),
                reminder.getReminderType().name(),
                reminder.getIsRead(),
                reminder.getRemindAt(),
                reminder.getCreatedAt()
        );
    }

    private int calculateCompletionRate(long totalTaskCount, long completedTaskCount) {
        if (totalTaskCount == 0) {
            return 0;
        }
        return (int) Math.round(completedTaskCount * 100.0 / totalTaskCount);
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
