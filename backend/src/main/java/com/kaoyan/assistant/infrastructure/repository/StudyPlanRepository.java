package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {

    List<StudyPlan> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<StudyPlan> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserId(Long userId);
}
