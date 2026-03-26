package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.SystemNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemNoticeRepository extends JpaRepository<SystemNotice, Long> {
    List<SystemNotice> findByTargetRoleInOrderByCreatedAtDesc(List<String> roles);
}
