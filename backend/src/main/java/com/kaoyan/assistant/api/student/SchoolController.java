package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.school.SchoolQueryService;
import com.kaoyan.assistant.application.school.dto.SchoolDetailResponse;
import com.kaoyan.assistant.application.school.dto.SchoolSummaryResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schools")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
public class SchoolController {

    private final SchoolQueryService schoolQueryService;

    public SchoolController(SchoolQueryService schoolQueryService) {
        this.schoolQueryService = schoolQueryService;
    }

    @GetMapping
    public ApiResponse<List<SchoolSummaryResponse>> listSchools(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(schoolQueryService.listSchools(keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<SchoolDetailResponse> getSchool(@PathVariable Long id) {
        return ApiResponse.success(schoolQueryService.getSchool(id));
    }
}
