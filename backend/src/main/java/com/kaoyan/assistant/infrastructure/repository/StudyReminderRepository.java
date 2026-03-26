package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.StudyReminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyReminderRepository extends JpaRepository<StudyReminder, Long> {

    List<StudyReminder> findByUserIdOrderByRemindAtDescCreatedAtDesc(Long userId);

    List<StudyReminder> findByUserIdAndPlanIdOrderByRemindAtDescCreatedAtDesc(Long userId, Long planId);
}
