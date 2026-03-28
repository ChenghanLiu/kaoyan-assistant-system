package com.kaoyan.assistant.application.system;

import com.kaoyan.assistant.application.system.dto.SystemNoticeRequest;
import com.kaoyan.assistant.application.system.dto.SystemNoticeResponse;
import com.kaoyan.assistant.application.system.dto.SystemNoticeUpdateRequest;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.domain.entity.SystemNotice;
import com.kaoyan.assistant.infrastructure.repository.SystemNoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class SystemNoticeService {

    private final SystemNoticeRepository systemNoticeRepository;
    private final OperationLogService operationLogService;

    public SystemNoticeService(SystemNoticeRepository systemNoticeRepository,
                               OperationLogService operationLogService) {
        this.systemNoticeRepository = systemNoticeRepository;
        this.operationLogService = operationLogService;
    }

    @Transactional(readOnly = true)
    public List<SystemNoticeResponse> listNotices(LoginUser loginUser) {
        if (loginUser != null && loginUser.getAuthorities().stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            return systemNoticeRepository.findAllByOrderByCreatedAtDescIdDesc().stream().map(this::toResponse).toList();
        }
        Set<String> targetRoles = new LinkedHashSet<>();
        targetRoles.add("ALL");
        if (loginUser != null) {
            loginUser.getAuthorities().forEach(authority -> targetRoles.add(authority.getAuthority().replace("ROLE_", "")));
        }
        return systemNoticeRepository.findByTargetRoleInOrderByCreatedAtDescIdDesc(targetRoles)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SystemNoticeResponse createNotice(LoginUser loginUser, SystemNoticeRequest request) {
        SystemNotice notice = new SystemNotice();
        notice.setTitle(request.title().trim());
        notice.setContent(request.content().trim());
        notice.setTargetRole(normalizeTargetRole(request.targetRole()));
        SystemNotice savedNotice = systemNoticeRepository.save(notice);
        operationLogService.record(loginUser, "NOTICE", "CREATE",
                "发布系统公告《" + savedNotice.getTitle() + "》", "/api/admin/notices");
        return toResponse(savedNotice);
    }

    @Transactional
    public SystemNoticeResponse updateNotice(LoginUser loginUser, Long noticeId, SystemNoticeUpdateRequest request) {
        SystemNotice notice = systemNoticeRepository.findById(noticeId)
                .orElseThrow(() -> BusinessException.notFound("system notice not found"));
        if (StringUtils.hasText(request.title())) {
            notice.setTitle(request.title().trim());
        }
        if (request.content() != null) {
            String content = request.content().trim();
            if (!StringUtils.hasText(content)) {
                throw BusinessException.invalidInput("content must not be blank");
            }
            notice.setContent(content);
        }
        if (StringUtils.hasText(request.targetRole())) {
            notice.setTargetRole(normalizeTargetRole(request.targetRole()));
        }
        SystemNotice savedNotice = systemNoticeRepository.save(notice);
        operationLogService.record(loginUser, "NOTICE", "UPDATE",
                "修改系统公告《" + savedNotice.getTitle() + "》", "/api/admin/notices/" + noticeId);
        return toResponse(savedNotice);
    }

    @Transactional
    public void deleteNotice(LoginUser loginUser, Long noticeId) {
        SystemNotice notice = systemNoticeRepository.findById(noticeId)
                .orElseThrow(() -> BusinessException.notFound("system notice not found"));
        systemNoticeRepository.delete(notice);
        operationLogService.record(loginUser, "NOTICE", "DELETE",
                "删除系统公告《" + notice.getTitle() + "》", "/api/admin/notices/" + noticeId);
    }

    private String normalizeTargetRole(String targetRole) {
        String normalized = targetRole.trim().toUpperCase(Locale.ROOT);
        if (!Set.of("ALL", "STUDENT", "ADMIN").contains(normalized)) {
            throw BusinessException.invalidInput("targetRole must be ALL, STUDENT or ADMIN");
        }
        return normalized;
    }

    private SystemNoticeResponse toResponse(SystemNotice notice) {
        return new SystemNoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getTargetRole(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }
}
