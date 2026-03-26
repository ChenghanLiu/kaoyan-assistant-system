package com.kaoyan.assistant.application.school;

import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.AdmissionGuide;
import com.kaoyan.assistant.domain.entity.ApplicationRatio;
import com.kaoyan.assistant.domain.entity.Major;
import com.kaoyan.assistant.domain.entity.PolicyNews;
import com.kaoyan.assistant.domain.entity.School;
import com.kaoyan.assistant.infrastructure.repository.AdmissionGuideRepository;
import com.kaoyan.assistant.infrastructure.repository.ApplicationRatioRepository;
import com.kaoyan.assistant.infrastructure.repository.MajorRepository;
import com.kaoyan.assistant.infrastructure.repository.PolicyNewsRepository;
import com.kaoyan.assistant.infrastructure.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final MajorRepository majorRepository;
    private final AdmissionGuideRepository admissionGuideRepository;
    private final ApplicationRatioRepository applicationRatioRepository;
    private final PolicyNewsRepository policyNewsRepository;

    public List<School> listSchools(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return schoolRepository.findAll();
        }
        return schoolRepository.findBySchoolNameContainingIgnoreCase(keyword);
    }

    public Map<String, Object> getSchoolDetail(Long schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new BusinessException("School not found"));
        List<AdmissionGuide> guides = admissionGuideRepository.findBySchoolId(schoolId);
        List<ApplicationRatio> ratios = applicationRatioRepository.findBySchoolId(schoolId);
        return Map.of("school", school, "guides", guides, "ratios", ratios);
    }

    public Major getMajorDetail(Long majorId) {
        return majorRepository.findById(majorId).orElseThrow(() -> new BusinessException("Major not found"));
    }

    public List<PolicyNews> listPolicies() {
        return policyNewsRepository.findAll();
    }
}
