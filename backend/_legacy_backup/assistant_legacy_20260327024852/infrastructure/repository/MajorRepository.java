package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorRepository extends JpaRepository<Major, Long> {
}
