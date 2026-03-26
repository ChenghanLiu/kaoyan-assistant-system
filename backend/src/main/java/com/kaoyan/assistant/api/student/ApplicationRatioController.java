package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.school.SchoolInformationService;
import com.kaoyan.assistant.application.school.dto.ApplicationRatioResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ratios")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
public class ApplicationRatioController {

    private final SchoolInformationService schoolInformationService;

    public ApplicationRatioController(SchoolInformationService schoolInformationService) {
        this.schoolInformationService = schoolInformationService;
    }

    @GetMapping
    public ApiResponse<List<ApplicationRatioResponse>> listRatios(@RequestParam(required = false) Long schoolId,
                                                                  @RequestParam(required = false) Long majorId) {
        return ApiResponse.success(schoolInformationService.listRatios(schoolId, majorId));
    }
}
