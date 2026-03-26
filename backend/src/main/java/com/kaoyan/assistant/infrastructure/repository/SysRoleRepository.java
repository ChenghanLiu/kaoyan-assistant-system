package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.SysRole;
import com.kaoyan.assistant.domain.enums.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SysRoleRepository extends JpaRepository<SysRole, Long> {

    Optional<SysRole> findByRoleCode(RoleCode roleCode);
}
