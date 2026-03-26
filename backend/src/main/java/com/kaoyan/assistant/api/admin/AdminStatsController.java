package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.system.AdminStatsService;
import com.kaoyan.assistant.application.system.dto.AdminStatsOverviewResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    public AdminStatsController(AdminStatsService adminStatsService) {
        this.adminStatsService = adminStatsService;
    }

    @GetMapping("/overview")
    public ApiResponse<AdminStatsOverviewResponse> overview() {
        return ApiResponse.success(adminStatsService.getOverview());
    }
}
