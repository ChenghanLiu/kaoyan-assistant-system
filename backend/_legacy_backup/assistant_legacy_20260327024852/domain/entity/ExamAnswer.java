package com.kaoyan.assistant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "exam_answer")
public class ExamAnswer extends BaseEntity {

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "selected_answer", length = 32)
    private String selectedAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean correct;
}
