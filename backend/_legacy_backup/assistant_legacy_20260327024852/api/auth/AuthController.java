package com.kaoyan.assistant.api.auth;

import com.kaoyan.assistant.application.auth.AuthService;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.common.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody AuthService.RegisterCommand command) {
        return ApiResponse.success("Register success", authService.register(command));
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody AuthService.LoginCommand command) {
        return ApiResponse.success("Login success", authService.login(command));
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return ApiResponse.success(authService.me(loginUser));
    }
}
