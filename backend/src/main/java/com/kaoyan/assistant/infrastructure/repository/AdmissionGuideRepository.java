package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.AdmissionGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdmissionGuideRepository extends JpaRepository<AdmissionGuide, Long> {

    @Query("""
            select guide from AdmissionGuide guide
            where (:schoolId is null or guide.schoolId = :schoolId)
              and (:majorId is null or guide.majorId = :majorId)
            order by guide.guideYear desc, guide.id desc
            """)
    List<AdmissionGuide> findAllByFilter(Long schoolId, Long majorId);

    List<AdmissionGuide> findBySchoolIdOrderByGuideYearDescIdDesc(Long schoolId);

    boolean existsBySchoolId(Long schoolId);

    boolean existsByMajorId(Long majorId);
}
