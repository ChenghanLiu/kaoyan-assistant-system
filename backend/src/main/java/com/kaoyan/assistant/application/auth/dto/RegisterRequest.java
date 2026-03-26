package com.kaoyan.assistant.application.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "username is required")
        @Size(min = 4, max = 64, message = "username length must be between 4 and 64")
        String username,
        @NotBlank(message = "password is required")
        @Size(min = 6, max = 32, message = "password length must be between 6 and 32")
        String password,
        @NotBlank(message = "displayName is required")
        @Size(max = 64, message = "displayName length must be at most 64")
        String displayName,
        @Email(message = "email format is invalid")
        String email,
        String phone,
        String targetSchool,
        String targetMajor
) {
}
