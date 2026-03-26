package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.system.SystemNoticeService;
import com.kaoyan.assistant.application.system.dto.SystemNoticeRequest;
import com.kaoyan.assistant.application.system.dto.SystemNoticeResponse;
import com.kaoyan.assistant.application.system.dto.SystemNoticeUpdateRequest;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/notices")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class AdminNoticeController {

    private final SystemNoticeService systemNoticeService;

    public AdminNoticeController(SystemNoticeService systemNoticeService) {
        this.systemNoticeService = systemNoticeService;
    }

    @PostMapping
    public ApiResponse<SystemNoticeResponse> createNotice(@Valid @RequestBody SystemNoticeRequest request) {
        return ApiResponse.success("notice created", systemNoticeService.createNotice(SecurityUtils.getLoginUser(), request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<SystemNoticeResponse> updateNotice(@PathVariable @Positive Long id,
                                                          @Valid @RequestBody SystemNoticeUpdateRequest request) {
        return ApiResponse.success("notice updated", systemNoticeService.updateNotice(SecurityUtils.getLoginUser(), id, request));
    }
}
