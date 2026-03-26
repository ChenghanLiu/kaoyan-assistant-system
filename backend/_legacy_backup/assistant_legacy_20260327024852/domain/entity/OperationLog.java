package com.kaoyan.assistant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "operation_log")
public class OperationLog extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 64)
    private String username;

    @Column(name = "operation_type", nullable = false, length = 64)
    private String operationType;

    @Column(name = "operation_content", nullable = false, length = 255)
    private String operationContent;
}
