package com.kaoyan.assistant.application.school;

import com.kaoyan.assistant.application.school.dto.MajorResponse;
import com.kaoyan.assistant.application.school.dto.SchoolSummaryResponse;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.Major;
import com.kaoyan.assistant.domain.entity.School;
import com.kaoyan.assistant.infrastructure.repository.AdmissionGuideRepository;
import com.kaoyan.assistant.infrastructure.repository.ApplicationRatioRepository;
import com.kaoyan.assistant.infrastructure.repository.MajorRepository;
import com.kaoyan.assistant.infrastructure.repository.PolicyNewsRepository;
import com.kaoyan.assistant.infrastructure.repository.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminSchoolManagementService {

    private final SchoolRepository schoolRepository;
    private final MajorRepository majorRepository;
    private final AdmissionGuideRepository admissionGuideRepository;
    private final ApplicationRatioRepository applicationRatioRepository;
    private final PolicyNewsRepository policyNewsRepository;

    public AdminSchoolManagementService(SchoolRepository schoolRepository,
                                        MajorRepository majorRepository,
                                        AdmissionGuideRepository admissionGuideRepository,
                                        ApplicationRatioRepository applicationRatioRepository,
                                        PolicyNewsRepository policyNewsRepository) {
        this.schoolRepository = schoolRepository;
        this.majorRepository = majorRepository;
        this.admissionGuideRepository = admissionGuideRepository;
        this.applicationRatioRepository = applicationRatioRepository;
        this.policyNewsRepository = policyNewsRepository;
    }

    @Transactional(readOnly = true)
    public List<SchoolSummaryResponse> listSchools() {
        return schoolRepository.findAll().stream()
                .map(this::toSchoolSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MajorResponse> listMajors() {
        return majorRepository.findAllByOrderByIdAsc().stream()
                .map(this::toMajorResponse)
                .toList();
    }

    @Transactional
    public void deleteSchool(Long schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> BusinessException.notFound("school not found"));
        if (majorRepository.existsBySchoolId(schoolId)) {
            throw BusinessException.invalidInput("school has related majors and cannot be deleted");
        }
        if (admissionGuideRepository.existsBySchoolId(schoolId)) {
            throw BusinessException.invalidInput("school has related admissions and cannot be deleted");
        }
        if (applicationRatioRepository.existsBySchoolId(schoolId)) {
            throw BusinessException.invalidInput("school has related ratios and cannot be deleted");
        }
        if (policyNewsRepository.existsBySchoolId(schoolId)) {
            throw BusinessException.invalidInput("school has related policies and cannot be deleted");
        }
        schoolRepository.delete(school);
    }

    @Transactional
    public void deleteMajor(Long majorId) {
        Major major = majorRepository.findById(majorId)
                .orElseThrow(() -> BusinessException.notFound("major not found"));
        if (admissionGuideRepository.existsByMajorId(majorId)) {
            throw BusinessException.invalidInput("major has related admissions and cannot be deleted");
        }
        if (applicationRatioRepository.existsByMajorId(majorId)) {
            throw BusinessException.invalidInput("major has related ratios and cannot be deleted");
        }
        if (policyNewsRepository.existsByMajorId(majorId)) {
            throw BusinessException.invalidInput("major has related policies and cannot be deleted");
        }
        majorRepository.delete(major);
    }

    private SchoolSummaryResponse toSchoolSummary(School school) {
        return new SchoolSummaryResponse(
                school.getId(),
                school.getSchoolName(),
                school.getProvince(),
                school.getCity(),
                school.getSchoolType(),
                school.getSchoolLevel(),
                school.getDescription()
        );
    }

    private MajorResponse toMajorResponse(Major major) {
        return new MajorResponse(
                major.getId(),
                major.getSchoolId(),
                major.getMajorName(),
                major.getMajorCode(),
                major.getDegreeType(),
                major.getDescription()
        );
    }
}
