package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major, Long> {

    List<Major> findAllByOrderByIdAsc();

    List<Major> findBySchoolIdOrderByIdAsc(Long schoolId);
}
