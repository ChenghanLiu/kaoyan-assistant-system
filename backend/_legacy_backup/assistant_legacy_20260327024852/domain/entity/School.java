package com.kaoyan.assistant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "school")
public class School extends BaseEntity {

    @Column(name = "school_name", nullable = false, length = 128)
    private String schoolName;

    @Column(name = "city", nullable = false, length = 64)
    private String city;

    @Column(name = "province", nullable = false, length = 64)
    private String province;

    @Column(name = "school_type", nullable = false, length = 64)
    private String schoolType;

    @Column(name = "school_level", length = 64)
    private String schoolLevel;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
