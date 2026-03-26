package com.kaoyan.assistant.application.school.dto;

import java.util.List;

public record SchoolDetailResponse(
        Long id,
        String schoolName,
        String province,
        String city,
        String schoolType,
        String schoolLevel,
        String description,
        List<MajorResponse> majors,
        List<AdmissionGuideResponse> admissionGuides,
        List<ApplicationRatioResponse> applicationRatios
) {
}
