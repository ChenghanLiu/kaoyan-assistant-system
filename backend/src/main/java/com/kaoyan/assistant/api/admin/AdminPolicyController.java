package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.school.AdminSchoolInformationService;
import com.kaoyan.assistant.application.school.dto.PolicyNewsCreateRequest;
import com.kaoyan.assistant.application.school.dto.PolicyNewsResponse;
import com.kaoyan.assistant.application.school.dto.PolicyNewsUpdateRequest;
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
@RequestMapping("/api/admin/policies")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPolicyController {

    private final AdminSchoolInformationService adminSchoolInformationService;

    public AdminPolicyController(AdminSchoolInformationService adminSchoolInformationService) {
        this.adminSchoolInformationService = adminSchoolInformationService;
    }

    @GetMapping
    public ApiResponse<List<PolicyNewsResponse>> listPolicies(@RequestParam(required = false) Long schoolId,
                                                              @RequestParam(required = false) Long majorId) {
        return ApiResponse.success(adminSchoolInformationService.listPolicies(schoolId, majorId));
    }

    @PostMapping
    public ApiResponse<PolicyNewsResponse> createPolicy(@Valid @RequestBody PolicyNewsCreateRequest request) {
        return ApiResponse.success("policy news created", adminSchoolInformationService.createPolicy(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<PolicyNewsResponse> updatePolicy(@PathVariable Long id,
                                                        @Valid @RequestBody PolicyNewsUpdateRequest request) {
        return ApiResponse.success("policy news updated", adminSchoolInformationService.updatePolicy(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePolicy(@PathVariable Long id) {
        adminSchoolInformationService.deletePolicy(id);
        return ApiResponse.success("policy news deleted", null);
    }
}
