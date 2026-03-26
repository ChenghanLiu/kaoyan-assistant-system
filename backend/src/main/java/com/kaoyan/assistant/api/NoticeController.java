package com.kaoyan.assistant.api;

import com.kaoyan.assistant.application.system.SystemNoticeService;
import com.kaoyan.assistant.application.system.dto.SystemNoticeResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
public class NoticeController {

    private final SystemNoticeService systemNoticeService;

    public NoticeController(SystemNoticeService systemNoticeService) {
        this.systemNoticeService = systemNoticeService;
    }

    @GetMapping
    public ApiResponse<List<SystemNoticeResponse>> listNotices() {
        return ApiResponse.success(systemNoticeService.listNotices(SecurityUtils.getLoginUser()));
    }
}
