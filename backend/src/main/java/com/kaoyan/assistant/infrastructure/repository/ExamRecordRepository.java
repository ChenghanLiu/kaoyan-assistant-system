package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.ExamRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExamRecordRepository extends JpaRepository<ExamRecord, Long> {

    List<ExamRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<ExamRecord> findByIdAndUserId(Long id, Long userId);

    @Query("select coalesce(avg(e.score), 0) from ExamRecord e")
    Double findAverageScore();

    @Query("select coalesce(max(e.score), 0) from ExamRecord e")
    Integer findHighestScore();

    @Query("select coalesce(min(e.score), 0) from ExamRecord e")
    Integer findLowestScore();

    boolean existsByPaperId(Long paperId);

    boolean existsByUserId(Long userId);
}
