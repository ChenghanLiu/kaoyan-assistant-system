package com.kaoyan.assistant.application.exam;

import com.kaoyan.assistant.application.exam.dto.AdminCreateOptionRequest;
import com.kaoyan.assistant.application.exam.dto.AdminCreatePaperRequest;
import com.kaoyan.assistant.application.exam.dto.AdminCreateQuestionRequest;
import com.kaoyan.assistant.application.exam.dto.AdminQuestionResponse;
import com.kaoyan.assistant.application.exam.dto.ExamOptionResponse;
import com.kaoyan.assistant.application.exam.dto.ExamPaperDetailResponse;
import com.kaoyan.assistant.application.exam.dto.ExamPaperSummaryResponse;
import com.kaoyan.assistant.application.exam.dto.ExamRecordAnswerResponse;
import com.kaoyan.assistant.application.exam.dto.ExamRecordDetailResponse;
import com.kaoyan.assistant.application.exam.dto.ExamRecordSummaryResponse;
import com.kaoyan.assistant.application.exam.dto.StudentExamQuestionResponse;
import com.kaoyan.assistant.application.exam.dto.SubmitExamRequest;
import com.kaoyan.assistant.application.exam.dto.SubmitExamResponse;
import com.kaoyan.assistant.application.exam.dto.WrongQuestionResponse;
import com.kaoyan.assistant.application.system.OperationLogService;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.domain.entity.ExamAnswer;
import com.kaoyan.assistant.domain.entity.ExamOption;
import com.kaoyan.assistant.domain.entity.ExamPaper;
import com.kaoyan.assistant.domain.entity.ExamQuestion;
import com.kaoyan.assistant.domain.entity.ExamRecord;
import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.domain.entity.WrongQuestion;
import com.kaoyan.assistant.infrastructure.repository.ExamAnswerRepository;
import com.kaoyan.assistant.infrastructure.repository.ExamOptionRepository;
import com.kaoyan.assistant.infrastructure.repository.ExamPaperRepository;
import com.kaoyan.assistant.infrastructure.repository.ExamQuestionRepository;
import com.kaoyan.assistant.infrastructure.repository.ExamRecordRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import com.kaoyan.assistant.infrastructure.repository.WrongQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private static final Set<String> TRUE_VALUES = Set.of("TRUE", "T", "YES", "Y", "RIGHT", "1", "对", "正确");
    private static final Set<String> FALSE_VALUES = Set.of("FALSE", "F", "NO", "N", "WRONG", "0", "错", "错误");

    private final ExamPaperRepository examPaperRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamOptionRepository examOptionRepository;
    private final ExamRecordRepository examRecordRepository;
    private final ExamAnswerRepository examAnswerRepository;
    private final WrongQuestionRepository wrongQuestionRepository;
    private final SysUserRepository sysUserRepository;
    private final OperationLogService operationLogService;

    public ExamService(ExamPaperRepository examPaperRepository,
                       ExamQuestionRepository examQuestionRepository,
                       ExamOptionRepository examOptionRepository,
                       ExamRecordRepository examRecordRepository,
                       ExamAnswerRepository examAnswerRepository,
                       WrongQuestionRepository wrongQuestionRepository,
                       SysUserRepository sysUserRepository,
                       OperationLogService operationLogService) {
        this.examPaperRepository = examPaperRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.examOptionRepository = examOptionRepository;
        this.examRecordRepository = examRecordRepository;
        this.examAnswerRepository = examAnswerRepository;
        this.wrongQuestionRepository = wrongQuestionRepository;
        this.sysUserRepository = sysUserRepository;
        this.operationLogService = operationLogService;
    }

    @Transactional(readOnly = true)
    public List<ExamPaperSummaryResponse> listStudentPapers() {
        return examPaperRepository.findAll().stream()
                .sorted((left, right) -> {
                    int yearCompare = compareNullableInteger(right.getPaperYear(), left.getPaperYear());
                    if (yearCompare != 0) {
                        return yearCompare;
                    }
                    return Long.compare(right.getId(), left.getId());
                })
                .map(this::toPaperSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExamPaperDetailResponse getStudentPaperDetail(Long paperId) {
        return toPaperDetail(requirePaper(paperId));
    }

    @Transactional(readOnly = true)
    public List<StudentExamQuestionResponse> listStudentQuestions(Long paperId) {
        requirePaper(paperId);
        List<ExamQuestion> questions = examQuestionRepository.findByPaperIdOrderBySortOrderAscIdAsc(paperId);
        Map<Long, List<ExamOptionResponse>> optionsMap = toOptionResponseMap(questions.stream().map(ExamQuestion::getId).toList());
        return questions.stream()
                .map(question -> new StudentExamQuestionResponse(
                        question.getId(),
                        question.getQuestionType(),
                        question.getQuestionStem(),
                        question.getScore(),
                        question.getSortOrder(),
                        optionsMap.getOrDefault(question.getId(), Collections.emptyList())
                ))
                .toList();
    }

    @Transactional
    public SubmitExamResponse submit(Long userId, SubmitExamRequest request) {
        ExamPaper paper = requirePaper(request.paperId());
        List<ExamQuestion> questions = examQuestionRepository.findByPaperIdOrderBySortOrderAscIdAsc(request.paperId());
        if (questions.isEmpty()) {
            throw BusinessException.invalidInput("paper has no questions");
        }

        Map<Long, SubmitExamRequest.SubmitAnswerRequest> answerRequestMap = request.answers().stream()
                .collect(Collectors.toMap(SubmitExamRequest.SubmitAnswerRequest::questionId, Function.identity(), (left, right) -> right, LinkedHashMap::new));
        Map<Long, Set<String>> optionLabelMap = loadQuestionOptionLabelMap(questions.stream().map(ExamQuestion::getId).toList());

        List<PendingAnswer> pendingAnswers = new ArrayList<>();
        int totalScore = 0;
        int score = 0;
        int correctCount = 0;
        for (ExamQuestion question : questions) {
            totalScore += question.getScore();
            SubmitExamRequest.SubmitAnswerRequest answerRequest = answerRequestMap.get(question.getId());
            List<String> selectedOptions = answerRequest == null ? Collections.emptyList() : normalizeSelectedOptions(answerRequest.selectedOptions());
            validateSelectedOptions(question, selectedOptions, optionLabelMap.getOrDefault(question.getId(), Collections.emptySet()));

            boolean correct = isCorrect(question, selectedOptions);
            int awardedScore = correct ? question.getScore() : 0;
            if (correct) {
                score += awardedScore;
                correctCount++;
            }
            pendingAnswers.add(new PendingAnswer(question, selectedOptions, correct, awardedScore));
        }

        ExamRecord record = new ExamRecord();
        record.setPaperId(paper.getId());
        record.setUserId(userId);
        record.setScore(score);
        record.setTotalScore(totalScore);
        record.setCorrectCount(correctCount);
        record.setTotalCount(questions.size());
        ExamRecord savedRecord = examRecordRepository.save(record);

        List<ExamAnswer> answers = new ArrayList<>();
        for (PendingAnswer pendingAnswer : pendingAnswers) {
            ExamAnswer answer = new ExamAnswer();
            answer.setRecordId(savedRecord.getId());
            answer.setQuestionId(pendingAnswer.question().getId());
            answer.setSelectedAnswer(joinAnswers(pendingAnswer.selectedOptions()));
            answer.setCorrect(pendingAnswer.correct());
            answer.setAwardedScore(pendingAnswer.awardedScore());
            answers.add(answer);
        }
        examAnswerRepository.saveAll(answers);

        for (PendingAnswer pendingAnswer : pendingAnswers) {
            if (!pendingAnswer.correct()) {
                WrongQuestion wrongQuestion = wrongQuestionRepository.findByUserIdAndQuestionId(userId, pendingAnswer.question().getId())
                        .orElseGet(WrongQuestion::new);
                wrongQuestion.setUserId(userId);
                wrongQuestion.setPaperId(paper.getId());
                wrongQuestion.setQuestionId(pendingAnswer.question().getId());
                wrongQuestion.setLatestRecordId(savedRecord.getId());
                wrongQuestion.setLatestAnswer(joinAnswers(pendingAnswer.selectedOptions()));
                wrongQuestionRepository.save(wrongQuestion);
            }
        }
        operationLogService.record(toLoginUser(userId), "EXAM", "SUBMIT",
                "提交模拟考试，试卷ID=" + paper.getId() + "，得分=" + score, "/api/student/exams/submit");

        return new SubmitExamResponse(savedRecord.getId(), score, totalScore, correctCount, questions.size());
    }

    @Transactional(readOnly = true)
    public List<ExamRecordSummaryResponse> listStudentRecords(Long userId) {
        List<ExamRecord> records = examRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
        Map<Long, ExamPaper> paperMap = loadPaperMap(records.stream().map(ExamRecord::getPaperId).toList());
        return records.stream()
                .map(record -> new ExamRecordSummaryResponse(
                        record.getId(),
                        record.getPaperId(),
                        paperTitle(paperMap.get(record.getPaperId())),
                        record.getScore(),
                        record.getTotalScore(),
                        record.getCorrectCount(),
                        record.getTotalCount(),
                        record.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ExamRecordDetailResponse getStudentRecordDetail(Long userId, Long recordId) {
        ExamRecord record = examRecordRepository.findByIdAndUserId(recordId, userId)
                .orElseThrow(() -> BusinessException.notFound("exam record not found"));
        ExamPaper paper = requirePaper(record.getPaperId());
        List<ExamAnswer> answers = examAnswerRepository.findByRecordIdOrderByIdAsc(recordId);
        Map<Long, ExamQuestion> questionMap = loadQuestionMap(answers.stream().map(ExamAnswer::getQuestionId).toList());
        Map<Long, List<ExamOptionResponse>> optionMap = toOptionResponseMap(questionMap.keySet());
        List<ExamRecordAnswerResponse> answerResponses = answers.stream()
                .map(answer -> toRecordAnswerResponse(answer, questionMap.get(answer.getQuestionId()), optionMap.getOrDefault(answer.getQuestionId(), Collections.emptyList())))
                .toList();
        return new ExamRecordDetailResponse(
                record.getId(),
                record.getPaperId(),
                paper.getTitle(),
                record.getScore(),
                record.getTotalScore(),
                record.getCorrectCount(),
                record.getTotalCount(),
                record.getCreatedAt(),
                answerResponses
        );
    }

    @Transactional(readOnly = true)
    public List<WrongQuestionResponse> listWrongQuestions(Long userId) {
        List<WrongQuestion> wrongQuestions = wrongQuestionRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        Map<Long, ExamQuestion> questionMap = loadQuestionMap(wrongQuestions.stream().map(WrongQuestion::getQuestionId).toList());
        Map<Long, ExamPaper> paperMap = loadPaperMap(wrongQuestions.stream().map(WrongQuestion::getPaperId).toList());
        Map<Long, List<ExamOptionResponse>> optionMap = toOptionResponseMap(questionMap.keySet());
        return wrongQuestions.stream()
                .map(wrongQuestion -> {
                    ExamQuestion question = questionMap.get(wrongQuestion.getQuestionId());
                    if (question == null) {
                        throw BusinessException.notFound("question not found");
                    }
                    return new WrongQuestionResponse(
                            wrongQuestion.getId(),
                            wrongQuestion.getPaperId(),
                            paperTitle(paperMap.get(wrongQuestion.getPaperId())),
                            question.getId(),
                            question.getQuestionType(),
                            question.getQuestionStem(),
                            question.getScore(),
                            splitAnswers(wrongQuestion.getLatestAnswer()),
                            splitAnswers(normalizeCorrectAnswer(question)),
                            question.getAnalysisText(),
                            optionMap.getOrDefault(question.getId(), Collections.emptyList()),
                            wrongQuestion.getUpdatedAt()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExamPaperSummaryResponse> listAdminPapers() {
        return listStudentPapers();
    }

    @Transactional
    public ExamPaperSummaryResponse createPaper(AdminCreatePaperRequest request) {
        ExamPaper paper = new ExamPaper();
        paper.setTitle(request.title().trim());
        paper.setDescription(normalizeText(request.description()));
        paper.setPaperYear(request.paperYear());
        paper.setQuestionCount(0);
        paper.setTotalScore(0);
        return toPaperSummary(examPaperRepository.save(paper));
    }

    @Transactional(readOnly = true)
    public List<AdminQuestionResponse> listAdminQuestions(Long paperId) {
        List<ExamQuestion> questions = paperId == null
                ? examQuestionRepository.findAllByOrderByCreatedAtDesc()
                : examQuestionRepository.findByPaperIdOrderBySortOrderAscIdAsc(paperId);
        Map<Long, ExamPaper> paperMap = loadPaperMap(questions.stream().map(ExamQuestion::getPaperId).toList());
        return questions.stream()
                .map(question -> new AdminQuestionResponse(
                        question.getId(),
                        question.getPaperId(),
                        paperTitle(paperMap.get(question.getPaperId())),
                        question.getQuestionType(),
                        question.getQuestionStem(),
                        normalizeCorrectAnswer(question),
                        question.getScore(),
                        question.getAnalysisText(),
                        question.getSortOrder(),
                        question.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public AdminQuestionResponse createQuestion(AdminCreateQuestionRequest request) {
        ExamPaper paper = requirePaper(request.paperId());
        String questionType = normalizeQuestionType(request.questionType());
        ExamQuestion question = new ExamQuestion();
        question.setPaperId(paper.getId());
        question.setQuestionType(questionType);
        question.setQuestionStem(request.questionStem().trim());
        question.setCorrectAnswer(normalizeCorrectAnswer(questionType, request.correctAnswer()));
        question.setScore(request.score());
        question.setAnalysisText(normalizeText(request.analysisText()));
        question.setSortOrder(request.sortOrder() == null ? nextSortOrder(paper.getId()) : request.sortOrder());
        ExamQuestion savedQuestion = examQuestionRepository.save(question);
        refreshPaperStats(paper.getId());
        return new AdminQuestionResponse(
                savedQuestion.getId(),
                savedQuestion.getPaperId(),
                paper.getTitle(),
                savedQuestion.getQuestionType(),
                savedQuestion.getQuestionStem(),
                savedQuestion.getCorrectAnswer(),
                savedQuestion.getScore(),
                savedQuestion.getAnalysisText(),
                savedQuestion.getSortOrder(),
                savedQuestion.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ExamOptionResponse> listAdminOptions(Long questionId) {
        requireQuestion(questionId);
        return examOptionRepository.findByQuestionIdOrderByOptionLabelAsc(questionId).stream()
                .map(this::toOptionResponse)
                .toList();
    }

    @Transactional
    public ExamOptionResponse createOption(Long questionId, AdminCreateOptionRequest request) {
        requireQuestion(questionId);
        ExamOption option = new ExamOption();
        option.setQuestionId(questionId);
        option.setOptionLabel(normalizeLabel(request.optionLabel()));
        option.setOptionContent(request.optionContent().trim());
        return toOptionResponse(examOptionRepository.save(option));
    }

    private ExamPaper requirePaper(Long paperId) {
        return examPaperRepository.findById(paperId)
                .orElseThrow(() -> BusinessException.notFound("exam paper not found"));
    }

    private ExamQuestion requireQuestion(Long questionId) {
        return examQuestionRepository.findById(questionId)
                .orElseThrow(() -> BusinessException.notFound("exam question not found"));
    }

    private ExamPaperSummaryResponse toPaperSummary(ExamPaper paper) {
        return new ExamPaperSummaryResponse(
                paper.getId(),
                paper.getTitle(),
                paper.getDescription(),
                paper.getPaperYear(),
                paper.getQuestionCount(),
                paper.getTotalScore(),
                paper.getCreatedAt()
        );
    }

    private ExamPaperDetailResponse toPaperDetail(ExamPaper paper) {
        return new ExamPaperDetailResponse(
                paper.getId(),
                paper.getTitle(),
                paper.getDescription(),
                paper.getPaperYear(),
                paper.getQuestionCount(),
                paper.getTotalScore(),
                paper.getCreatedAt()
        );
    }

    private ExamRecordAnswerResponse toRecordAnswerResponse(ExamAnswer answer, ExamQuestion question, List<ExamOptionResponse> options) {
        if (question == null) {
            throw BusinessException.notFound("question not found");
        }
        return new ExamRecordAnswerResponse(
                question.getId(),
                question.getQuestionType(),
                question.getQuestionStem(),
                question.getScore(),
                answer.getAwardedScore(),
                Boolean.TRUE.equals(answer.getCorrect()),
                splitAnswers(answer.getSelectedAnswer()),
                splitAnswers(normalizeCorrectAnswer(question)),
                question.getAnalysisText(),
                options
        );
    }

    private Map<Long, List<ExamOptionResponse>> toOptionResponseMap(Collection<Long> questionIds) {
        if (questionIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return examOptionRepository.findByQuestionIdInOrderByQuestionIdAscOptionLabelAsc(questionIds).stream()
                .collect(Collectors.groupingBy(
                        ExamOption::getQuestionId,
                        LinkedHashMap::new,
                        Collectors.mapping(this::toOptionResponse, Collectors.toList())
                ));
    }

    private ExamOptionResponse toOptionResponse(ExamOption option) {
        return new ExamOptionResponse(option.getId(), option.getOptionLabel(), option.getOptionContent());
    }

    private Map<Long, Set<String>> loadQuestionOptionLabelMap(Collection<Long> questionIds) {
        if (questionIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return examOptionRepository.findByQuestionIdInOrderByQuestionIdAscOptionLabelAsc(questionIds).stream()
                .collect(Collectors.groupingBy(
                        ExamOption::getQuestionId,
                        LinkedHashMap::new,
                        Collectors.mapping(option -> normalizeLabel(option.getOptionLabel()), Collectors.toCollection(LinkedHashSet::new))
                ));
    }

    private Map<Long, ExamPaper> loadPaperMap(Collection<Long> paperIds) {
        if (paperIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return examPaperRepository.findAllById(new LinkedHashSet<>(paperIds)).stream()
                .collect(Collectors.toMap(ExamPaper::getId, Function.identity()));
    }

    private Map<Long, ExamQuestion> loadQuestionMap(Collection<Long> questionIds) {
        if (questionIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return examQuestionRepository.findAllById(new LinkedHashSet<>(questionIds)).stream()
                .collect(Collectors.toMap(ExamQuestion::getId, Function.identity()));
    }

    private void refreshPaperStats(Long paperId) {
        ExamPaper paper = requirePaper(paperId);
        List<ExamQuestion> questions = examQuestionRepository.findByPaperIdOrderBySortOrderAscIdAsc(paperId);
        int totalScore = questions.stream().map(ExamQuestion::getScore).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        paper.setQuestionCount(questions.size());
        paper.setTotalScore(totalScore);
        examPaperRepository.save(paper);
    }

    private int nextSortOrder(Long paperId) {
        return examQuestionRepository.findByPaperIdOrderBySortOrderAscIdAsc(paperId).stream()
                .map(ExamQuestion::getSortOrder)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private boolean isCorrect(ExamQuestion question, List<String> selectedOptions) {
        String questionType = normalizeQuestionType(question.getQuestionType());
        String normalizedCorrectAnswer = normalizeCorrectAnswer(question);
        if ("MULTIPLE".equals(questionType)) {
            return normalizedCorrectAnswer.equals(joinAnswers(selectedOptions));
        }
        if ("JUDGE".equals(questionType)) {
            if (selectedOptions.isEmpty()) {
                return false;
            }
            return normalizeJudgeValue(normalizedCorrectAnswer).equals(normalizeJudgeValue(selectedOptions.get(0)));
        }
        if (selectedOptions.isEmpty()) {
            return false;
        }
        return normalizedCorrectAnswer.equals(normalizeLabel(selectedOptions.get(0)));
    }

    private void validateSelectedOptions(ExamQuestion question, List<String> selectedOptions, Set<String> validLabels) {
        String questionType = normalizeQuestionType(question.getQuestionType());
        if ("SINGLE".equals(questionType) || "JUDGE".equals(questionType)) {
            if (selectedOptions.size() > 1) {
                throw BusinessException.invalidInput("single choice question can only have one answer");
            }
        }
        for (String selectedOption : selectedOptions) {
            if (!validLabels.isEmpty() && !validLabels.contains(normalizeLabel(selectedOption)) && !"JUDGE".equals(questionType)) {
                throw BusinessException.invalidInput("selected answer is invalid");
            }
        }
    }

    private String normalizeCorrectAnswer(ExamQuestion question) {
        return normalizeCorrectAnswer(normalizeQuestionType(question.getQuestionType()), question.getCorrectAnswer());
    }

    private String normalizeCorrectAnswer(String questionType, String correctAnswer) {
        if (!StringUtils.hasText(correctAnswer)) {
            throw BusinessException.invalidInput("correctAnswer is required");
        }
        if ("MULTIPLE".equals(questionType)) {
            return joinAnswers(splitAndNormalize(correctAnswer));
        }
        if ("JUDGE".equals(questionType)) {
            return normalizeJudgeValue(correctAnswer);
        }
        return normalizeLabel(correctAnswer);
    }

    private String normalizeQuestionType(String questionType) {
        String normalized = normalizeLabel(questionType);
        if (!Set.of("SINGLE", "MULTIPLE", "JUDGE").contains(normalized)) {
            throw BusinessException.invalidInput("questionType must be SINGLE, MULTIPLE or JUDGE");
        }
        return normalized;
    }

    private List<String> normalizeSelectedOptions(List<String> selectedOptions) {
        if (selectedOptions == null || selectedOptions.isEmpty()) {
            return Collections.emptyList();
        }
        return selectedOptions.stream()
                .filter(StringUtils::hasText)
                .map(this::normalizeLabel)
                .distinct()
                .sorted()
                .toList();
    }

    private List<String> splitAndNormalize(String rawAnswer) {
        if (!StringUtils.hasText(rawAnswer)) {
            return Collections.emptyList();
        }
        return List.of(rawAnswer.split(",")).stream()
                .filter(StringUtils::hasText)
                .map(this::normalizeLabel)
                .distinct()
                .sorted()
                .toList();
    }

    private List<String> splitAnswers(String value) {
        return splitAndNormalize(value);
    }

    private String joinAnswers(List<String> values) {
        return values == null || values.isEmpty() ? "" : String.join(",", values);
    }

    private String normalizeJudgeValue(String value) {
        String normalized = normalizeLabel(value);
        if (TRUE_VALUES.contains(normalized)) {
            return "TRUE";
        }
        if (FALSE_VALUES.contains(normalized)) {
            return "FALSE";
        }
        throw BusinessException.invalidInput("judge answer must be true or false");
    }

    private String normalizeLabel(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String paperTitle(ExamPaper paper) {
        return paper == null ? "" : paper.getTitle();
    }

    private int compareNullableInteger(Integer left, Integer right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        if (right == null) {
            return 1;
        }
        return Integer.compare(left, right);
    }

    private LoginUser toLoginUser(Long userId) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("user not found"));
        return new LoginUser(user);
    }

    private record PendingAnswer(ExamQuestion question, List<String> selectedOptions, boolean correct, int awardedScore) {
    }
}
