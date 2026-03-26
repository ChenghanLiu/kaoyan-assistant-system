package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.domain.enums.RoleCode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<SysUser> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("""
            select count(distinct u.id)
            from SysUser u
            join u.roles r
            where r.roleCode = :roleCode
            """)
    long countDistinctByRoleCode(@Param("roleCode") RoleCode roleCode);
}
