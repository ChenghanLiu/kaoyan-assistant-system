package com.kaoyan.assistant.api.auth;

import com.kaoyan.assistant.application.auth.AuthService;
import com.kaoyan.assistant.application.auth.dto.AuthResponse;
import com.kaoyan.assistant.application.auth.dto.LoginRequest;
import com.kaoyan.assistant.application.auth.dto.RegisterRequest;
import com.kaoyan.assistant.application.auth.dto.UserProfileResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("register success", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("login success", authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> me() {
        return ApiResponse.success(authService.me(SecurityUtils.getLoginUser()));
    }
}
