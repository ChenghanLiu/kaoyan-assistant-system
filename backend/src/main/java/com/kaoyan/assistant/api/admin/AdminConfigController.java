package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.system.SystemConfigService;
import com.kaoyan.assistant.application.system.dto.SystemConfigResponse;
import com.kaoyan.assistant.application.system.dto.SystemConfigUpdateRequest;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/configs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminConfigController {

    private final SystemConfigService systemConfigService;

    public AdminConfigController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    @GetMapping
    public ApiResponse<List<SystemConfigResponse>> listConfigs() {
        return ApiResponse.success(systemConfigService.listConfigs());
    }

    @PatchMapping("/{key}")
    public ApiResponse<SystemConfigResponse> updateConfig(@PathVariable String key,
                                                          @Valid @RequestBody SystemConfigUpdateRequest request) {
        return ApiResponse.success("config updated", systemConfigService.updateConfig(SecurityUtils.getLoginUser(), key, request));
    }

    @DeleteMapping("/{key}")
    public ApiResponse<Void> deleteConfig(@PathVariable String key) {
        systemConfigService.deleteConfig(SecurityUtils.getLoginUser(), key);
        return ApiResponse.success("config deleted", null);
    }
}
