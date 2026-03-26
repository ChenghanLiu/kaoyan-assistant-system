package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.dashboard.AdminDashboardService;
import com.kaoyan.assistant.common.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public ApiResponse<?> dashboard() {
        return ApiResponse.success(adminDashboardService.getDashboard());
    }
}
