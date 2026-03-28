package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.PolicyNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PolicyNewsRepository extends JpaRepository<PolicyNews, Long> {

    @Query("""
            select news from PolicyNews news
            where (:schoolId is null or news.schoolId = :schoolId)
              and (:majorId is null or news.majorId = :majorId)
            order by news.publishedDate desc, news.id desc
            """)
    List<PolicyNews> findAllByFilter(Long schoolId, Long majorId);

    boolean existsBySchoolId(Long schoolId);

    boolean existsByMajorId(Long majorId);
}
