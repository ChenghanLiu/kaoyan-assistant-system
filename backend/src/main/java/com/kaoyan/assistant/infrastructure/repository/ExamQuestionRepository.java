package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {

    List<ExamQuestion> findByPaperIdOrderBySortOrderAscIdAsc(Long paperId);

    List<ExamQuestion> findByPaperIdInOrderByPaperIdAscSortOrderAscIdAsc(List<Long> paperIds);

    List<ExamQuestion> findAllByOrderByCreatedAtDesc();

    long countByPaperId(Long paperId);
}
