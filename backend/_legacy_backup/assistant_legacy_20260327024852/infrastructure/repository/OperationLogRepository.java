package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
}
