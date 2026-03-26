package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.domain.entity.ExamPaper;
import com.kaoyan.assistant.domain.entity.ExamQuestion;
import com.kaoyan.assistant.infrastructure.repository.ExamPaperRepository;
import com.kaoyan.assistant.infrastructure.repository.ExamQuestionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminExamController {

    private final ExamPaperRepository examPaperRepository;
    private final ExamQuestionRepository examQuestionRepository;

    @GetMapping("/papers")
    public ApiResponse<?> listPapers() {
        return ApiResponse.success(examPaperRepository.findAll());
    }

    @PostMapping("/papers")
    public ApiResponse<?> createPaper(@Valid @RequestBody PaperRequest request) {
        ExamPaper paper = new ExamPaper();
        paper.setPaperName(request.paperName());
        paper.setPaperDescription(request.paperDescription());
        paper.setTotalScore(request.totalScore());
        paper.setDurationMinutes(request.durationMinutes());
        return ApiResponse.success("Create success", examPaperRepository.save(paper));
    }

    @GetMapping("/papers/{paperId}/questions")
    public ApiResponse<?> listQuestions(@PathVariable Long paperId) {
        return ApiResponse.success(examQuestionRepository.findByPaperIdOrderByIdAsc(paperId));
    }

    @PostMapping("/papers/{paperId}/questions")
    public ApiResponse<?> createQuestion(@PathVariable Long paperId, @Valid @RequestBody QuestionRequest request) {
        if (!examPaperRepository.existsById(paperId)) {
            throw new BusinessException("Paper not found");
        }
        ExamQuestion question = new ExamQuestion();
        question.setPaperId(paperId);
        question.setQuestionType(request.questionType());
        question.setQuestionStem(request.questionStem());
        question.setCorrectAnswer(request.correctAnswer());
        question.setScore(request.score());
        question.setAnalysisText(request.analysisText());
        return ApiResponse.success("Create success", examQuestionRepository.save(question));
    }

    @PutMapping("/questions/{questionId}")
    public ApiResponse<?> updateQuestion(@PathVariable Long questionId, @Valid @RequestBody QuestionRequest request) {
        ExamQuestion question = examQuestionRepository.findById(questionId).orElseThrow(() -> new BusinessException("Question not found"));
        question.setQuestionType(request.questionType());
        question.setQuestionStem(request.questionStem());
        question.setCorrectAnswer(request.correctAnswer());
        question.setScore(request.score());
        question.setAnalysisText(request.analysisText());
        return ApiResponse.success("Update success", examQuestionRepository.save(question));
    }

    public record PaperRequest(@NotBlank String paperName, String paperDescription, Integer totalScore, Integer durationMinutes) {
    }

    public record QuestionRequest(@NotBlank String questionType, @NotBlank String questionStem,
                                  @NotBlank String correctAnswer, Integer score, String analysisText) {
    }
}
