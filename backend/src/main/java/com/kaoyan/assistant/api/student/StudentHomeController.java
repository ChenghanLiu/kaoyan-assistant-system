package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.dashboard.StudentDashboardService;
import com.kaoyan.assistant.application.dashboard.dto.StudentHomeSummaryResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/home")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
public class StudentHomeController {

    private final StudentDashboardService studentDashboardService;

    public StudentHomeController(StudentDashboardService studentDashboardService) {
        this.studentDashboardService = studentDashboardService;
    }

    @GetMapping("/summary")
    public ApiResponse<StudentHomeSummaryResponse> summary() {
        return ApiResponse.success(studentDashboardService.getSummary());
    }
}
