package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.user.AdminUserService;
import com.kaoyan.assistant.application.user.dto.AdminUserResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ApiResponse<List<AdminUserResponse>> listUsers() {
        return ApiResponse.success(adminUserService.listUsers());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id, SecurityUtils.getLoginUser().getId());
        return ApiResponse.success("user deleted", null);
    }
}
