package com.kaoyan.assistant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "exam_paper")
public class ExamPaper extends BaseEntity {

    @Column(name = "paper_name", nullable = false, length = 200)
    private String paperName;

    @Column(name = "paper_description", columnDefinition = "TEXT")
    private String paperDescription;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
}
