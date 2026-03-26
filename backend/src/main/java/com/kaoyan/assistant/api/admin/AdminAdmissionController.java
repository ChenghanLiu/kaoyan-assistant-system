package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.school.AdminSchoolInformationService;
import com.kaoyan.assistant.application.school.dto.AdmissionGuideCreateRequest;
import com.kaoyan.assistant.application.school.dto.AdmissionGuideResponse;
import com.kaoyan.assistant.application.school.dto.AdmissionGuideUpdateRequest;
import com.kaoyan.assistant.common.model.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/admissions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAdmissionController {

    private final AdminSchoolInformationService adminSchoolInformationService;

    public AdminAdmissionController(AdminSchoolInformationService adminSchoolInformationService) {
        this.adminSchoolInformationService = adminSchoolInformationService;
    }

    @GetMapping
    public ApiResponse<List<AdmissionGuideResponse>> listAdmissions(@RequestParam(required = false) Long schoolId,
                                                                    @RequestParam(required = false) Long majorId) {
        return ApiResponse.success(adminSchoolInformationService.listAdmissions(schoolId, majorId));
    }

    @PostMapping
    public ApiResponse<AdmissionGuideResponse> createAdmission(@Valid @RequestBody AdmissionGuideCreateRequest request) {
        return ApiResponse.success("admission guide created", adminSchoolInformationService.createAdmission(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<AdmissionGuideResponse> updateAdmission(@PathVariable Long id,
                                                               @Valid @RequestBody AdmissionGuideUpdateRequest request) {
        return ApiResponse.success("admission guide updated", adminSchoolInformationService.updateAdmission(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAdmission(@PathVariable Long id) {
        adminSchoolInformationService.deleteAdmission(id);
        return ApiResponse.success("admission guide deleted", null);
    }
}
