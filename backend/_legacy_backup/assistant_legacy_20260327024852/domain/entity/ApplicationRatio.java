package com.kaoyan.assistant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "application_ratio")
public class ApplicationRatio extends BaseEntity {

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "major_id", nullable = false)
    private Long majorId;

    @Column(name = "ratio_year", nullable = false)
    private Integer ratioYear;

    @Column(name = "apply_count", nullable = false)
    private Integer applyCount;

    @Column(name = "admit_count", nullable = false)
    private Integer admitCount;

    @Column(name = "ratio_value", nullable = false, length = 32)
    private String ratioValue;
}
