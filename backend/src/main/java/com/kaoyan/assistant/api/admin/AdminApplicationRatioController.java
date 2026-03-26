package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.school.AdminSchoolInformationService;
import com.kaoyan.assistant.application.school.dto.ApplicationRatioCreateRequest;
import com.kaoyan.assistant.application.school.dto.ApplicationRatioResponse;
import com.kaoyan.assistant.application.school.dto.ApplicationRatioUpdateRequest;
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
@RequestMapping("/api/admin/ratios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApplicationRatioController {

    private final AdminSchoolInformationService adminSchoolInformationService;

    public AdminApplicationRatioController(AdminSchoolInformationService adminSchoolInformationService) {
        this.adminSchoolInformationService = adminSchoolInformationService;
    }

    @GetMapping
    public ApiResponse<List<ApplicationRatioResponse>> listRatios(@RequestParam(required = false) Long schoolId,
                                                                  @RequestParam(required = false) Long majorId) {
        return ApiResponse.success(adminSchoolInformationService.listRatios(schoolId, majorId));
    }

    @PostMapping
    public ApiResponse<ApplicationRatioResponse> createRatio(@Valid @RequestBody ApplicationRatioCreateRequest request) {
        return ApiResponse.success("application ratio created", adminSchoolInformationService.createRatio(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<ApplicationRatioResponse> updateRatio(@PathVariable Long id,
                                                             @Valid @RequestBody ApplicationRatioUpdateRequest request) {
        return ApiResponse.success("application ratio updated", adminSchoolInformationService.updateRatio(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRatio(@PathVariable Long id) {
        adminSchoolInformationService.deleteRatio(id);
        return ApiResponse.success("application ratio deleted", null);
    }
}
