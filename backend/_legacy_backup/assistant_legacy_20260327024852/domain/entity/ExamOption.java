package com.kaoyan.assistant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "exam_option")
public class ExamOption extends BaseEntity {

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "option_label", nullable = false, length = 8)
    private String optionLabel;

    @Column(name = "option_content", nullable = false, columnDefinition = "TEXT")
    private String optionContent;
}
