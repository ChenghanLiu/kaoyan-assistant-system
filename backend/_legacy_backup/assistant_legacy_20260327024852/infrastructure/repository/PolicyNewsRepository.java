package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.PolicyNews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyNewsRepository extends JpaRepository<PolicyNews, Long> {
}
