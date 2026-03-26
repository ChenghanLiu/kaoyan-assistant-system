package com.kaoyan.assistant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Integer getRatioYear() {
        return ratioYear;
    }

    public void setRatioYear(Integer ratioYear) {
        this.ratioYear = ratioYear;
    }

    public Integer getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(Integer applyCount) {
        this.applyCount = applyCount;
    }

    public Integer getAdmitCount() {
        return admitCount;
    }

    public void setAdmitCount(Integer admitCount) {
        this.admitCount = admitCount;
    }

    public String getRatioValue() {
        return ratioValue;
    }

    public void setRatioValue(String ratioValue) {
        this.ratioValue = ratioValue;
    }
}
