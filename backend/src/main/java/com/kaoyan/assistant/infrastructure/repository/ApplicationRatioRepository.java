package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.ApplicationRatio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationRatioRepository extends JpaRepository<ApplicationRatio, Long> {

    @Query("""
            select ratio from ApplicationRatio ratio
            where (:schoolId is null or ratio.schoolId = :schoolId)
              and (:majorId is null or ratio.majorId = :majorId)
            order by ratio.ratioYear desc, ratio.id desc
            """)
    List<ApplicationRatio> findAllByFilter(Long schoolId, Long majorId);

    List<ApplicationRatio> findBySchoolIdOrderByRatioYearDescIdDesc(Long schoolId);

    boolean existsBySchoolId(Long schoolId);

    boolean existsByMajorId(Long majorId);
}
