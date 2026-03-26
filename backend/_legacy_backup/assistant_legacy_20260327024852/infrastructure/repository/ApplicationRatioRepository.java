package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.ApplicationRatio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRatioRepository extends JpaRepository<ApplicationRatio, Long> {
    List<ApplicationRatio> findBySchoolId(Long schoolId);
}
