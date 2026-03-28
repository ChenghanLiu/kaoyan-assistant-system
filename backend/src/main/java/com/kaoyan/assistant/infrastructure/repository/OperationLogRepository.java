package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {

    List<OperationLog> findTop200ByOrderByCreatedAtDescIdDesc();

    boolean existsByUserId(Long userId);
}
