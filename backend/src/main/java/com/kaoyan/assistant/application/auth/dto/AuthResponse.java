package com.kaoyan.assistant.application.auth.dto;

public record AuthResponse(String token, UserProfileResponse user) {
}
