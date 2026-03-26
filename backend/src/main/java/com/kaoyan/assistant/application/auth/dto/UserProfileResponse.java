package com.kaoyan.assistant.application.auth.dto;

import java.util.List;

public record UserProfileResponse(
        Long id,
        String username,
        String displayName,
        String email,
        String phone,
        String targetSchool,
        String targetMajor,
        List<String> roles
) {
}
