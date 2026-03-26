package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.AdmissionGuide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdmissionGuideRepository extends JpaRepository<AdmissionGuide, Long> {
    List<AdmissionGuide> findBySchoolId(Long schoolId);
}
