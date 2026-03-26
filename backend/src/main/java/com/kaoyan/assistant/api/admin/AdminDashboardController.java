package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.dashboard.AdminDashboardService;
import com.kaoyan.assistant.application.dashboard.dto.AdminDashboardSummaryResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/summary")
    public ApiResponse<AdminDashboardSummaryResponse> summary() {
        return ApiResponse.success(adminDashboardService.getSummary());
    }
}
