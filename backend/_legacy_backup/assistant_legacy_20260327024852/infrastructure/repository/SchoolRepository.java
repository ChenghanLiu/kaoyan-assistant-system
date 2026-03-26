package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Long> {
    List<School> findBySchoolNameContainingIgnoreCase(String keyword);
}
