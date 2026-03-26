package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.exam.ExamService;
import com.kaoyan.assistant.application.exam.dto.AdminCreateOptionRequest;
import com.kaoyan.assistant.application.exam.dto.AdminCreatePaperRequest;
import com.kaoyan.assistant.application.exam.dto.AdminCreateQuestionRequest;
import com.kaoyan.assistant.application.exam.dto.AdminQuestionResponse;
import com.kaoyan.assistant.application.exam.dto.ExamOptionResponse;
import com.kaoyan.assistant.application.exam.dto.ExamPaperSummaryResponse;
import com.kaoyan.assistant.common.model.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminExamController {

    private final ExamService examService;

    public AdminExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/papers")
    public ApiResponse<List<ExamPaperSummaryResponse>> listPapers() {
        return ApiResponse.success(examService.listAdminPapers());
    }

    @PostMapping("/papers")
    public ApiResponse<ExamPaperSummaryResponse> createPaper(@Valid @RequestBody AdminCreatePaperRequest request) {
        return ApiResponse.success("create success", examService.createPaper(request));
    }

    @GetMapping("/questions")
    public ApiResponse<List<AdminQuestionResponse>> listQuestions(@RequestParam(required = false) Long paperId) {
        return ApiResponse.success(examService.listAdminQuestions(paperId));
    }

    @PostMapping("/questions")
    public ApiResponse<AdminQuestionResponse> createQuestion(@Valid @RequestBody AdminCreateQuestionRequest request) {
        return ApiResponse.success("create success", examService.createQuestion(request));
    }

    @GetMapping("/questions/{id}/options")
    public ApiResponse<List<ExamOptionResponse>> listOptions(@PathVariable Long id) {
        return ApiResponse.success(examService.listAdminOptions(id));
    }

    @PostMapping("/questions/{id}/options")
    public ApiResponse<ExamOptionResponse> createOption(@PathVariable Long id, @Valid @RequestBody AdminCreateOptionRequest request) {
        return ApiResponse.success("create success", examService.createOption(id, request));
    }
}
