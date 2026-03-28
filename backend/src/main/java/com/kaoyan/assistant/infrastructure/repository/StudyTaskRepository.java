package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.StudyTask;
import com.kaoyan.assistant.domain.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {

    List<StudyTask> findByPlanIdOrderByDueDateAscCreatedAtAsc(Long planId);

    List<StudyTask> findByPlanIdAndUserIdOrderByDueDateAscCreatedAtAsc(Long planId, Long userId);

    List<StudyTask> findByUserIdOrderByDueDateAscCreatedAtAsc(Long userId);

    Optional<StudyTask> findByIdAndUserId(Long id, Long userId);

    long countByPlanId(Long planId);

    long countByPlanIdAndStatus(Long planId, TaskStatus status);

    long countByPlanIdAndUserId(Long planId, Long userId);

    long countByPlanIdAndUserIdAndStatus(Long planId, Long userId, TaskStatus status);

    long countByStatus(TaskStatus status);

    boolean existsByUserId(Long userId);
}
