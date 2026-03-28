package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.ExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamPaperRepository extends JpaRepository<ExamPaper, Long> {

    boolean existsByTitleIgnoreCase(String title);
}
