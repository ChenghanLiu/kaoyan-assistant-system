package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.StudyProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyProgressRepository extends JpaRepository<StudyProgress, Long> {

    List<StudyProgress> findByTaskIdOrderByRecordedAtDesc(Long taskId);

    List<StudyProgress> findByTaskIdAndUserIdOrderByRecordedAtDesc(Long taskId, Long userId);

    List<StudyProgress> findByPlanIdOrderByRecordedAtDesc(Long planId);

    List<StudyProgress> findByPlanIdAndUserIdOrderByRecordedAtDesc(Long planId, Long userId);
}
