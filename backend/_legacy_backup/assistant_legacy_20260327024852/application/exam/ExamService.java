package com.kaoyan.assistant.application.exam;

import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.ExamAnswer;
import com.kaoyan.assistant.domain.entity.ExamOption;
import com.kaoyan.assistant.domain.entity.ExamPaper;
import com.kaoyan.assistant.domain.entity.ExamQuestion;
import com.kaoyan.assistant.domain.entity.ExamRecord;
import com.kaoyan.assistant.domain.entity.WrongQuestion;
import com.kaoyan.assistant.infrastructure.repository.ExamAnswerRepository;
import com.kaoyan.assistant.infrastructure.repository.ExamOptionRepository;
import com.kaoyan.assistant.infrastructure.repository.ExamPaperRepository;
import com.kaoyan.assistant.infrastructure.repository.ExamQuestionRepository;
import com.kaoyan.assistant.infrastructure.repository.ExamRecordRepository;
import com.kaoyan.assistant.infrastructure.repository.WrongQuestionRepository;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamPaperRepository examPaperRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamOptionRepository examOptionRepository;
    private final ExamRecordRepository examRecordRepository;
    private final ExamAnswerRepository examAnswerRepository;
    private final WrongQuestionRepository wrongQuestionRepository;

    public List<ExamPaper> listPapers() {
        return examPaperRepository.findAll();
    }

    public Map<String, Object> getPaperDetail(Long paperId) {
        ExamPaper paper = examPaperRepository.findById(paperId).orElseThrow(() -> new BusinessException("Paper not found"));
        List<ExamQuestion> questions = examQuestionRepository.findByPaperIdOrderByIdAsc(paperId);
        List<Map<String, Object>> questionViews = questions.stream().map(question -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", question.getId());
            item.put("questionType", question.getQuestionType());
            item.put("questionStem", question.getQuestionStem());
            item.put("score", question.getScore());
            item.put("analysisText", question.getAnalysisText());
            item.put("options", examOptionRepository.findByQuestionIdOrderByOptionLabelAsc(question.getId()));
            return item;
        }).toList();
        return Map.of("paper", paper, "questions", questionViews);
    }

    public Map<String, Object> submit(Long userId, SubmitCommand command) {
        ExamPaper paper = examPaperRepository.findById(command.paperId())
                .orElseThrow(() -> new BusinessException("Paper not found"));
        List<ExamQuestion> questions = examQuestionRepository.findByPaperIdOrderByIdAsc(command.paperId());
        Map<Long, String> answerMap = new HashMap<>();
        command.answers().forEach(answer -> answerMap.put(answer.questionId(), answer.selectedAnswer()));

        int score = 0;
        int correctCount = 0;
        List<ExamAnswer> answers = new ArrayList<>();
        for (ExamQuestion question : questions) {
            String selectedAnswer = answerMap.get(question.getId());
            boolean correct = question.getCorrectAnswer().equalsIgnoreCase(selectedAnswer == null ? "" : selectedAnswer);
            if (correct) {
                score += question.getScore();
                correctCount++;
            } else {
                WrongQuestion wrongQuestion = wrongQuestionRepository.findByUserIdAndQuestionId(userId, question.getId())
                        .orElseGet(WrongQuestion::new);
                wrongQuestion.setUserId(userId);
                wrongQuestion.setPaperId(paper.getId());
                wrongQuestion.setQuestionId(question.getId());
                wrongQuestion.setLatestAnswer(selectedAnswer);
                wrongQuestionRepository.save(wrongQuestion);
            }
            ExamAnswer examAnswer = new ExamAnswer();
            examAnswer.setQuestionId(question.getId());
            examAnswer.setSelectedAnswer(selectedAnswer);
            examAnswer.setCorrect(correct);
            answers.add(examAnswer);
        }
        ExamRecord record = new ExamRecord();
        record.setPaperId(paper.getId());
        record.setUserId(userId);
        record.setScore(score);
        record.setCorrectCount(correctCount);
        record.setTotalCount(questions.size());
        record = examRecordRepository.save(record);
        for (ExamAnswer answer : answers) {
            answer.setRecordId(record.getId());
        }
        examAnswerRepository.saveAll(answers);
        return Map.of("recordId", record.getId(), "score", score, "correctCount", correctCount, "totalCount", questions.size());
    }

    public List<ExamRecord> listResults(Long userId) {
        return examRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Map<String, Object>> listWrongQuestions(Long userId) {
        return wrongQuestionRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream().map(wrongQuestion -> {
            ExamQuestion question = examQuestionRepository.findById(wrongQuestion.getQuestionId())
                    .orElseThrow(() -> new BusinessException("Question not found"));
            List<ExamOption> options = examOptionRepository.findByQuestionIdOrderByOptionLabelAsc(question.getId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("wrongQuestionId", wrongQuestion.getId());
            item.put("paperId", wrongQuestion.getPaperId());
            item.put("questionId", question.getId());
            item.put("questionStem", question.getQuestionStem());
            item.put("analysisText", question.getAnalysisText());
            item.put("correctAnswer", question.getCorrectAnswer());
            item.put("latestAnswer", wrongQuestion.getLatestAnswer());
            item.put("options", options);
            return item;
        }).toList();
    }

    public record SubmitCommand(Long paperId, @NotEmpty List<AnswerCommand> answers) {
    }

    public record AnswerCommand(Long questionId, String selectedAnswer) {
    }
}
