package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.major.MajorQueryService;
import com.kaoyan.assistant.application.school.dto.MajorResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/majors")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
public class MajorController {

    private final MajorQueryService majorQueryService;

    public MajorController(MajorQueryService majorQueryService) {
        this.majorQueryService = majorQueryService;
    }

    @GetMapping
    public ApiResponse<List<MajorResponse>> listMajors() {
        return ApiResponse.success(majorQueryService.listMajors());
    }
}
