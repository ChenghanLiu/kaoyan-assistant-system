package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.ExamOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamOptionRepository extends JpaRepository<ExamOption, Long> {
    List<ExamOption> findByQuestionIdOrderByOptionLabelAsc(Long questionId);
}
