package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.plan.StudyPlanService;
import com.kaoyan.assistant.application.plan.dto.CreateStudyPlanRequest;
import com.kaoyan.assistant.application.plan.dto.CreateStudyProgressRequest;
import com.kaoyan.assistant.application.plan.dto.CreateStudyTaskRequest;
import com.kaoyan.assistant.application.plan.dto.StudyPlanDetailResponse;
import com.kaoyan.assistant.application.plan.dto.StudyPlanSummaryResponse;
import com.kaoyan.assistant.application.plan.dto.StudyProgressResponse;
import com.kaoyan.assistant.application.plan.dto.StudyReminderResponse;
import com.kaoyan.assistant.application.plan.dto.StudyTaskResponse;
import com.kaoyan.assistant.application.plan.dto.StudyTaskStatusRequest;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
@Validated
public class StudentPlanController {

    private final StudyPlanService studyPlanService;

    public StudentPlanController(StudyPlanService studyPlanService) {
        this.studyPlanService = studyPlanService;
    }

    @PostMapping("/plans")
    public ApiResponse<StudyPlanSummaryResponse> createPlan(@Valid @RequestBody CreateStudyPlanRequest request) {
        return ApiResponse.success("create success", studyPlanService.createPlan(currentUserId(), request));
    }

    @GetMapping("/plans")
    public ApiResponse<List<StudyPlanSummaryResponse>> listPlans() {
        return ApiResponse.success(studyPlanService.listPlans(currentUserId()));
    }

    @GetMapping("/plans/{id}")
    public ApiResponse<StudyPlanDetailResponse> getPlanDetail(@PathVariable @Positive(message = "id must be greater than 0") Long id) {
        return ApiResponse.success(studyPlanService.getPlanDetail(currentUserId(), id));
    }

    @PostMapping("/plans/{id}/tasks")
    public ApiResponse<StudyTaskResponse> createTask(@PathVariable @Positive(message = "id must be greater than 0") Long id,
                                                     @Valid @RequestBody CreateStudyTaskRequest request) {
        return ApiResponse.success("create success", studyPlanService.createTask(currentUserId(), id, request));
    }

    @GetMapping("/plans/{id}/tasks")
    public ApiResponse<List<StudyTaskResponse>> listPlanTasks(@PathVariable @Positive(message = "id must be greater than 0") Long id) {
        return ApiResponse.success(studyPlanService.listPlanTasks(currentUserId(), id));
    }

    @PatchMapping("/tasks/{id}/status")
    public ApiResponse<StudyTaskResponse> updateTaskStatus(@PathVariable @Positive(message = "id must be greater than 0") Long id,
                                                           @Valid @RequestBody StudyTaskStatusRequest request) {
        return ApiResponse.success("update success", studyPlanService.updateTaskStatus(currentUserId(), id, request.status()));
    }

    @PostMapping("/tasks/{id}/progress")
    public ApiResponse<StudyProgressResponse> recordProgress(@PathVariable @Positive(message = "id must be greater than 0") Long id,
                                                             @Valid @RequestBody CreateStudyProgressRequest request) {
        return ApiResponse.success("create success", studyPlanService.recordProgress(currentUserId(), id, request));
    }

    @GetMapping("/reminders")
    public ApiResponse<List<StudyReminderResponse>> listReminders() {
        return ApiResponse.success(studyPlanService.listReminders(currentUserId()));
    }

    private Long currentUserId() {
        return SecurityUtils.getLoginUser().getId();
    }
}
