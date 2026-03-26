package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.plan.StudyPlanService;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentPlanController {

    private final StudyPlanService studyPlanService;

    @GetMapping("/plans")
    public ApiResponse<?> listPlans() {
        return ApiResponse.success(studyPlanService.listPlans(SecurityUtils.getLoginUser().getId()));
    }

    @PostMapping("/plans")
    public ApiResponse<?> createPlan(@Valid @RequestBody StudyPlanService.PlanCommand command) {
        return ApiResponse.success("Create success", studyPlanService.createPlan(SecurityUtils.getLoginUser().getId(), command));
    }

    @PutMapping("/plans/{planId}")
    public ApiResponse<?> updatePlan(@PathVariable Long planId, @Valid @RequestBody StudyPlanService.PlanCommand command) {
        return ApiResponse.success("Update success", studyPlanService.updatePlan(SecurityUtils.getLoginUser().getId(), planId, command));
    }

    @DeleteMapping("/plans/{planId}")
    public ApiResponse<?> deletePlan(@PathVariable Long planId) {
        studyPlanService.deletePlan(SecurityUtils.getLoginUser().getId(), planId);
        return ApiResponse.success("Delete success", null);
    }

    @GetMapping("/plans/{planId}")
    public ApiResponse<?> getPlan(@PathVariable Long planId) {
        return ApiResponse.success(studyPlanService.getPlanOverview(SecurityUtils.getLoginUser().getId(), planId));
    }

    @GetMapping("/tasks")
    public ApiResponse<?> listTasks() {
        return ApiResponse.success(studyPlanService.listTasks(SecurityUtils.getLoginUser().getId()));
    }

    @PostMapping("/tasks")
    public ApiResponse<?> createTask(@Valid @RequestBody StudyPlanService.TaskCommand command) {
        return ApiResponse.success("Create success", studyPlanService.createTask(SecurityUtils.getLoginUser().getId(), command));
    }

    @PutMapping("/tasks/{taskId}")
    public ApiResponse<?> updateTask(@PathVariable Long taskId, @Valid @RequestBody StudyPlanService.TaskCommand command) {
        return ApiResponse.success("Update success", studyPlanService.updateTask(SecurityUtils.getLoginUser().getId(), taskId, command));
    }
}
