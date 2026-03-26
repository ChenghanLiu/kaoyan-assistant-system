package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.school.SchoolService;
import com.kaoyan.assistant.common.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentSchoolController {

    private final SchoolService schoolService;

    @GetMapping("/schools")
    public ApiResponse<?> listSchools(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(schoolService.listSchools(keyword));
    }

    @GetMapping("/schools/{schoolId}")
    public ApiResponse<?> schoolDetail(@PathVariable Long schoolId) {
        return ApiResponse.success(schoolService.getSchoolDetail(schoolId));
    }

    @GetMapping("/majors/{majorId}")
    public ApiResponse<?> majorDetail(@PathVariable Long majorId) {
        return ApiResponse.success(schoolService.getMajorDetail(majorId));
    }

    @GetMapping("/policies")
    public ApiResponse<?> policies() {
        return ApiResponse.success(schoolService.listPolicies());
    }
}
