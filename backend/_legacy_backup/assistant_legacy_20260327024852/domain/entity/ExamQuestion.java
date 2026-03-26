package com.kaoyan.assistant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "exam_question")
public class ExamQuestion extends BaseEntity {

    @Column(name = "paper_id", nullable = false)
    private Long paperId;

    @Column(name = "question_type", nullable = false, length = 32)
    private String questionType;

    @Column(name = "question_stem", nullable = false, columnDefinition = "TEXT")
    private String questionStem;

    @Column(name = "correct_answer", nullable = false, length = 32)
    private String correctAnswer;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "analysis_text", columnDefinition = "TEXT")
    private String analysisText;
}
