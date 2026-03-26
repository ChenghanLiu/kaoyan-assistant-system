package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.school.SchoolInformationService;
import com.kaoyan.assistant.application.school.dto.AdmissionGuideResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admissions")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
public class AdmissionController {

    private final SchoolInformationService schoolInformationService;

    public AdmissionController(SchoolInformationService schoolInformationService) {
        this.schoolInformationService = schoolInformationService;
    }

    @GetMapping
    public ApiResponse<List<AdmissionGuideResponse>> listAdmissions(@RequestParam(required = false) Long schoolId,
                                                                    @RequestParam(required = false) Long majorId) {
        return ApiResponse.success(schoolInformationService.listAdmissions(schoolId, majorId));
    }

    @GetMapping("/{id}")
    public ApiResponse<AdmissionGuideResponse> getAdmission(@PathVariable Long id) {
        return ApiResponse.success(schoolInformationService.getAdmission(id));
    }
}
