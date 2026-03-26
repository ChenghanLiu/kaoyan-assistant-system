package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.material.MaterialService;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.domain.entity.PolicyNews;
import com.kaoyan.assistant.domain.entity.SystemConfig;
import com.kaoyan.assistant.domain.entity.SystemNotice;
import com.kaoyan.assistant.domain.enums.MaterialStatus;
import com.kaoyan.assistant.infrastructure.repository.MaterialRepository;
import com.kaoyan.assistant.infrastructure.repository.OperationLogRepository;
import com.kaoyan.assistant.infrastructure.repository.PolicyNewsRepository;
import com.kaoyan.assistant.infrastructure.repository.SystemConfigRepository;
import com.kaoyan.assistant.infrastructure.repository.SystemNoticeRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminContentController {

    private final PolicyNewsRepository policyNewsRepository;
    private final SystemNoticeRepository systemNoticeRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final OperationLogRepository operationLogRepository;
    private final MaterialRepository materialRepository;
    private final MaterialService materialService;

    @GetMapping("/policy-news")
    public ApiResponse<?> listPolicyNews() {
        return ApiResponse.success(policyNewsRepository.findAll());
    }

    @PostMapping("/policy-news")
    public ApiResponse<?> createPolicyNews(@Valid @RequestBody PolicyNewsRequest request) {
        PolicyNews news = new PolicyNews();
        news.setTitle(request.title());
        news.setContent(request.content());
        news.setSourceName(request.sourceName());
        news.setPublishedDate(request.publishedDate());
        return ApiResponse.success("Create success", policyNewsRepository.save(news));
    }

    @GetMapping("/notices")
    public ApiResponse<?> listNotices() {
        return ApiResponse.success(systemNoticeRepository.findAll());
    }

    @PostMapping("/notices")
    public ApiResponse<?> createNotice(@Valid @RequestBody NoticeRequest request) {
        SystemNotice notice = new SystemNotice();
        notice.setTitle(request.title());
        notice.setContent(request.content());
        notice.setTargetRole(request.targetRole());
        return ApiResponse.success("Create success", systemNoticeRepository.save(notice));
    }

    @GetMapping("/system-configs")
    public ApiResponse<?> listConfigs() {
        return ApiResponse.success(systemConfigRepository.findAll());
    }

    @PutMapping("/system-configs/{configId}")
    public ApiResponse<?> updateConfig(@PathVariable Long configId, @Valid @RequestBody ConfigRequest request) {
        SystemConfig config = systemConfigRepository.findById(configId).orElseThrow(() -> new BusinessException("Config not found"));
        config.setConfigValue(request.configValue());
        config.setConfigDescription(request.configDescription());
        return ApiResponse.success("Update success", systemConfigRepository.save(config));
    }

    @GetMapping("/operation-logs")
    public ApiResponse<?> listLogs() {
        return ApiResponse.success(operationLogRepository.findAll());
    }

    @GetMapping("/materials/reviews")
    public ApiResponse<?> listPendingMaterials() {
        return ApiResponse.success(materialRepository.findByReviewStatusInOrderByCreatedAtDesc(
                List.of(MaterialStatus.PENDING, MaterialStatus.APPROVED, MaterialStatus.REJECTED)));
    }

    @PatchMapping("/materials/{materialId}/review")
    public ApiResponse<?> reviewMaterial(@PathVariable Long materialId, @RequestBody ReviewRequest request) {
        return ApiResponse.success("Review success", materialService.review(materialId, request.reviewStatus()));
    }

    public record PolicyNewsRequest(@NotBlank String title, String content, String sourceName, String publishedDate) {
    }

    public record NoticeRequest(@NotBlank String title, @NotBlank String content, String targetRole) {
    }

    public record ConfigRequest(@NotBlank String configValue, String configDescription) {
    }

    public record ReviewRequest(MaterialStatus reviewStatus) {
    }
}
