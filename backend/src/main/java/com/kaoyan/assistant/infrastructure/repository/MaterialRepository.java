package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.Material;
import com.kaoyan.assistant.domain.enums.MaterialStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByReviewStatusOrderByCreatedAtDesc(MaterialStatus reviewStatus);

    List<Material> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Material> findByIdAndUserId(Long id, Long userId);

    Optional<Material> findByIdAndReviewStatus(Long id, MaterialStatus reviewStatus);

    List<Material> findAllByOrderByCreatedAtDesc();

    long countByReviewStatus(MaterialStatus reviewStatus);
}
