package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.ExamRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRecordRepository extends JpaRepository<ExamRecord, Long> {
    List<ExamRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
}
