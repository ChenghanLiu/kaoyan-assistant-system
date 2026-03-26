package com.kaoyan.assistant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "major")
public class Major extends BaseEntity {

    @Column(name = "major_name", nullable = false, length = 128)
    private String majorName;

    @Column(name = "major_code", nullable = false, length = 32)
    private String majorCode;

    @Column(name = "category_name", nullable = false, length = 64)
    private String categoryName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
