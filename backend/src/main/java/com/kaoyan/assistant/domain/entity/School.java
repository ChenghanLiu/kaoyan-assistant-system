package com.kaoyan.assistant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
@Entity
@Table(name = "school")
public class School extends BaseEntity {

    @Column(name = "school_name", nullable = false, length = 128)
    private String schoolName;

    @Column(name = "province", nullable = false, length = 64)
    private String province;

    @Column(name = "city", nullable = false, length = 64)
    private String city;

    @Column(name = "school_type", nullable = false, length = 64)
    private String schoolType;

    @Column(name = "school_level", length = 64)
    private String schoolLevel;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSchoolType() {
        return schoolType;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }

    public String getSchoolLevel() {
        return schoolLevel;
    }

    public void setSchoolLevel(String schoolLevel) {
        this.schoolLevel = schoolLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
