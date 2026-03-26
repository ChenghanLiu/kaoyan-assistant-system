package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.StudyTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {
    List<StudyTask> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<StudyTask> findByPlanIdOrderByCreatedAtAsc(Long planId);
}
