package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.school.SchoolQueryService;
import com.kaoyan.assistant.application.school.dto.SchoolSummaryResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/schools")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSchoolController {

    private final SchoolQueryService schoolQueryService;

    public AdminSchoolController(SchoolQueryService schoolQueryService) {
        this.schoolQueryService = schoolQueryService;
    }

    @GetMapping
    public ApiResponse<List<SchoolSummaryResponse>> listSchools() {
        return ApiResponse.success(schoolQueryService.listSchools(null));
    }
}
