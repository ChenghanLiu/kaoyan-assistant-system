package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.ExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, Long> {

    List<ExamAnswer> findByRecordIdOrderByIdAsc(Long recordId);

    List<ExamAnswer> findByRecordIdInOrderByRecordIdAscIdAsc(Collection<Long> recordIds);
}
