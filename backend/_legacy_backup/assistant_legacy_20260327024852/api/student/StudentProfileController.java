package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.common.util.SecurityUtils;
import com.kaoyan.assistant.infrastructure.repository.SystemNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentProfileController {

    private final SystemNoticeRepository systemNoticeRepository;

    @GetMapping("/profile")
    public ApiResponse<?> profile() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", loginUser.getId());
        result.put("username", loginUser.getUsername());
        result.put("realName", loginUser.getRealName());
        result.put("roles", loginUser.getRoleCodes());
        result.put("targetSchool", loginUser.getUser().getTargetSchool());
        result.put("targetMajor", loginUser.getUser().getTargetMajor());
        return ApiResponse.success(result);
    }

    @GetMapping("/notices")
    public ApiResponse<?> notices() {
        return ApiResponse.success(systemNoticeRepository.findByTargetRoleInOrderByCreatedAtDesc(List.of("STUDENT", "ALL")));
    }
}
