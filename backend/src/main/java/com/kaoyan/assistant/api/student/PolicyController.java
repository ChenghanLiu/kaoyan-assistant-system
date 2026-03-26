package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.school.SchoolInformationService;
import com.kaoyan.assistant.application.school.dto.PolicyNewsResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
public class PolicyController {

    private final SchoolInformationService schoolInformationService;

    public PolicyController(SchoolInformationService schoolInformationService) {
        this.schoolInformationService = schoolInformationService;
    }

    @GetMapping
    public ApiResponse<List<PolicyNewsResponse>> listPolicies(@RequestParam(required = false) Long schoolId,
                                                              @RequestParam(required = false) Long majorId) {
        return ApiResponse.success(schoolInformationService.listPolicies(schoolId, majorId));
    }
}
