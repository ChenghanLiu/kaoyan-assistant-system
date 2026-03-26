package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
}
