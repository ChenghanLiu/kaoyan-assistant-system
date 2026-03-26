package com.kaoyan.assistant.application.user.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AdminUserResponse(
        Long id,
        String username,
        String displayName,
        String email,
        String phone,
        boolean enabled,
        String targetSchool,
        String targetMajor,
        List<String> roles,
        LocalDateTime createdAt
) {
}
