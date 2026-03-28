package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.auth.dto.UserProfileResponse;
import com.kaoyan.assistant.application.user.StudentProfileService;
import com.kaoyan.assistant.application.user.dto.StudentProfileUpdateRequest;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
@Validated
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    public StudentProfileController(StudentProfileService studentProfileService) {
        this.studentProfileService = studentProfileService;
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> profile() {
        return ApiResponse.success(studentProfileService.getProfile(currentUserId()));
    }

    @PatchMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(@Valid @RequestBody StudentProfileUpdateRequest request) {
        return ApiResponse.success("update success", studentProfileService.updateProfile(currentUserId(), request));
    }

    private Long currentUserId() {
        return SecurityUtils.getLoginUser().getId();
    }
}
