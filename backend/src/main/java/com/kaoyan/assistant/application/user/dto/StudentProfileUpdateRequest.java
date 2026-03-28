package com.kaoyan.assistant.application.user.dto;

import jakarta.validation.constraints.Size;

public record StudentProfileUpdateRequest(
        @Size(max = 64, message = "displayName length must be at most 64")
        String displayName,
        @Size(max = 128, message = "targetSchool length must be at most 128")
        String targetSchool,
        @Size(max = 128, message = "targetMajor length must be at most 128")
        String targetMajor
) {
}
