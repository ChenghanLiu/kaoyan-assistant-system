package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.Material;
import com.kaoyan.assistant.domain.enums.MaterialStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByReviewStatusOrderByCreatedAtDesc(MaterialStatus reviewStatus);
    List<Material> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Material> findByReviewStatusInOrderByCreatedAtDesc(List<MaterialStatus> statuses);
}
