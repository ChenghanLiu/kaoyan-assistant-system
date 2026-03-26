package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.exam.ExamService;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/exams")
@RequiredArgsConstructor
public class StudentExamController {

    private final ExamService examService;

    @GetMapping("/papers")
    public ApiResponse<?> listPapers() {
        return ApiResponse.success(examService.listPapers());
    }

    @GetMapping("/papers/{paperId}")
    public ApiResponse<?> paperDetail(@PathVariable Long paperId) {
        return ApiResponse.success(examService.getPaperDetail(paperId));
    }

    @PostMapping("/submit")
    public ApiResponse<?> submit(@Valid @RequestBody ExamService.SubmitCommand command) {
        return ApiResponse.success("Submit success", examService.submit(SecurityUtils.getLoginUser().getId(), command));
    }

    @GetMapping("/records")
    public ApiResponse<?> records() {
        return ApiResponse.success(examService.listResults(SecurityUtils.getLoginUser().getId()));
    }

    @GetMapping("/wrong-questions")
    public ApiResponse<?> wrongQuestions() {
        return ApiResponse.success(examService.listWrongQuestions(SecurityUtils.getLoginUser().getId()));
    }
}
