package com.kaoyan.assistant.domain.entity;

import com.kaoyan.assistant.domain.enums.RoleCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sys_role")
public class SysRole extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "role_code", nullable = false, unique = true, length = 32)
    private RoleCode roleCode;

    @Column(name = "role_name", nullable = false, length = 64)
    private String roleName;
}
