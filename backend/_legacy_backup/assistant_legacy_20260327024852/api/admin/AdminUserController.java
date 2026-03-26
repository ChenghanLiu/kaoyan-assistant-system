package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.user.AdminUserService;
import com.kaoyan.assistant.common.model.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ApiResponse<?> listUsers() {
        return ApiResponse.success(adminUserService.listUsers());
    }

    @PatchMapping("/{userId}/enabled")
    public ApiResponse<?> changeEnabled(@PathVariable Long userId, @RequestBody EnabledRequest request) {
        return ApiResponse.success("Update success", adminUserService.changeEnabled(userId, request.enabled()));
    }

    public record EnabledRequest(@NotNull Boolean enabled) {
    }
}
