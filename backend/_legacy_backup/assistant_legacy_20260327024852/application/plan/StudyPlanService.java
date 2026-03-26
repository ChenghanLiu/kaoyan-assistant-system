package com.kaoyan.assistant.application.plan;

import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.StudyPlan;
import com.kaoyan.assistant.domain.entity.StudyTask;
import com.kaoyan.assistant.domain.enums.TaskStatus;
import com.kaoyan.assistant.infrastructure.repository.StudyPlanRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyTaskRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final StudyTaskRepository studyTaskRepository;

    public List<StudyPlan> listPlans(Long userId) {
        return studyPlanRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public StudyPlan createPlan(Long userId, PlanCommand command) {
        StudyPlan plan = new StudyPlan();
        plan.setUserId(userId);
        plan.setPlanName(command.planName());
        plan.setPlanDescription(command.planDescription());
        plan.setStartDate(command.startDate());
        plan.setEndDate(command.endDate());
        return studyPlanRepository.save(plan);
    }

    public StudyPlan updatePlan(Long userId, Long planId, PlanCommand command) {
        StudyPlan plan = requireOwnedPlan(userId, planId);
        plan.setPlanName(command.planName());
        plan.setPlanDescription(command.planDescription());
        plan.setStartDate(command.startDate());
        plan.setEndDate(command.endDate());
        return studyPlanRepository.save(plan);
    }

    public void deletePlan(Long userId, Long planId) {
        StudyPlan plan = requireOwnedPlan(userId, planId);
        studyPlanRepository.delete(plan);
    }

    public List<StudyTask> listTasks(Long userId) {
        return studyTaskRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public StudyTask createTask(Long userId, TaskCommand command) {
        requireOwnedPlan(userId, command.planId());
        StudyTask task = new StudyTask();
        task.setUserId(userId);
        task.setPlanId(command.planId());
        task.setTaskName(command.taskName());
        task.setTaskDescription(command.taskDescription());
        task.setDueDate(command.dueDate());
        task.setStatus(command.status() == null ? TaskStatus.TODO : command.status());
        return studyTaskRepository.save(task);
    }

    public StudyTask updateTask(Long userId, Long taskId, TaskCommand command) {
        StudyTask task = requireOwnedTask(userId, taskId);
        task.setTaskName(command.taskName());
        task.setTaskDescription(command.taskDescription());
        task.setDueDate(command.dueDate());
        task.setStatus(command.status());
        return studyTaskRepository.save(task);
    }

    public Map<String, Object> getPlanOverview(Long userId, Long planId) {
        StudyPlan plan = requireOwnedPlan(userId, planId);
        List<StudyTask> tasks = studyTaskRepository.findByPlanIdOrderByCreatedAtAsc(planId);
        long doneCount = tasks.stream().filter(task -> task.getStatus() == TaskStatus.DONE).count();
        return Map.of("plan", plan, "tasks", tasks, "doneCount", doneCount, "totalCount", tasks.size());
    }

    private StudyPlan requireOwnedPlan(Long userId, Long planId) {
        StudyPlan plan = studyPlanRepository.findById(planId).orElseThrow(() -> new BusinessException("Plan not found"));
        if (!plan.getUserId().equals(userId)) {
            throw new BusinessException("Plan access denied");
        }
        return plan;
    }

    private StudyTask requireOwnedTask(Long userId, Long taskId) {
        StudyTask task = studyTaskRepository.findById(taskId).orElseThrow(() -> new BusinessException("Task not found"));
        if (!task.getUserId().equals(userId)) {
            throw new BusinessException("Task access denied");
        }
        return task;
    }

    public record PlanCommand(@NotBlank String planName, String planDescription, String startDate, String endDate) {
    }

    public record TaskCommand(Long planId, @NotBlank String taskName, String taskDescription, String dueDate,
                              TaskStatus status) {
    }
}
