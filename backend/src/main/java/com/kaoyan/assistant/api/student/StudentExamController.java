package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.exam.ExamService;
import com.kaoyan.assistant.application.exam.dto.ExamPaperDetailResponse;
import com.kaoyan.assistant.application.exam.dto.ExamPaperSummaryResponse;
import com.kaoyan.assistant.application.exam.dto.ExamRecordDetailResponse;
import com.kaoyan.assistant.application.exam.dto.ExamRecordSummaryResponse;
import com.kaoyan.assistant.application.exam.dto.StudentExamQuestionResponse;
import com.kaoyan.assistant.application.exam.dto.SubmitExamRequest;
import com.kaoyan.assistant.application.exam.dto.SubmitExamResponse;
import com.kaoyan.assistant.application.exam.dto.WrongQuestionResponse;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/exams")
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
public class StudentExamController {

    private final ExamService examService;

    public StudentExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/papers")
    public ApiResponse<List<ExamPaperSummaryResponse>> listPapers() {
        return ApiResponse.success(examService.listStudentPapers());
    }

    @GetMapping("/papers/{id}")
    public ApiResponse<ExamPaperDetailResponse> getPaper(@PathVariable Long id) {
        return ApiResponse.success(examService.getStudentPaperDetail(id));
    }

    @GetMapping("/papers/{id}/questions")
    public ApiResponse<List<StudentExamQuestionResponse>> listQuestions(@PathVariable Long id) {
        return ApiResponse.success(examService.listStudentQuestions(id));
    }

    @PostMapping("/submit")
    public ApiResponse<SubmitExamResponse> submit(@Valid @RequestBody SubmitExamRequest request) {
        Long userId = requireLoginUserId();
        return ApiResponse.success("submit success", examService.submit(userId, request));
    }

    @GetMapping("/records")
    public ApiResponse<List<ExamRecordSummaryResponse>> listRecords() {
        Long userId = requireLoginUserId();
        return ApiResponse.success(examService.listStudentRecords(userId));
    }

    @GetMapping("/records/{id}")
    public ApiResponse<ExamRecordDetailResponse> getRecord(@PathVariable Long id) {
        Long userId = requireLoginUserId();
        return ApiResponse.success(examService.getStudentRecordDetail(userId, id));
    }

    @GetMapping("/wrong-questions")
    public ApiResponse<List<WrongQuestionResponse>> listWrongQuestions() {
        Long userId = requireLoginUserId();
        return ApiResponse.success(examService.listWrongQuestions(userId));
    }

    private Long requireLoginUserId() {
        if (SecurityUtils.getLoginUser() == null) {
            throw BusinessException.forbidden("forbidden");
        }
        return SecurityUtils.getLoginUser().getId();
    }
}
