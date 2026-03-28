package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    List<SystemConfig> findAllByOrderByConfigKeyAsc();

    Optional<SystemConfig> findByConfigKey(String configKey);

    boolean existsByConfigKey(String configKey);
}
