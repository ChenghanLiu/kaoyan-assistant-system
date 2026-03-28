package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.school.AdminSchoolManagementService;
import com.kaoyan.assistant.application.school.dto.MajorResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/majors")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMajorController {

    private final AdminSchoolManagementService adminSchoolManagementService;

    public AdminMajorController(AdminSchoolManagementService adminSchoolManagementService) {
        this.adminSchoolManagementService = adminSchoolManagementService;
    }

    @GetMapping
    public ApiResponse<List<MajorResponse>> listMajors() {
        return ApiResponse.success(adminSchoolManagementService.listMajors());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMajor(@PathVariable Long id) {
        adminSchoolManagementService.deleteMajor(id);
        return ApiResponse.success("major deleted", null);
    }
}
