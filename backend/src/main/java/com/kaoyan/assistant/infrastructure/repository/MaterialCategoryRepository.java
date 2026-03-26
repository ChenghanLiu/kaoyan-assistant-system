package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.MaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialCategoryRepository extends JpaRepository<MaterialCategory, Long> {

    List<MaterialCategory> findAllByOrderBySortOrderAscIdAsc();
}
