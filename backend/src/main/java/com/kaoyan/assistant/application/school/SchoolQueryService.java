package com.kaoyan.assistant.application.school;

import com.kaoyan.assistant.application.school.dto.AdmissionGuideResponse;
import com.kaoyan.assistant.application.school.dto.ApplicationRatioResponse;
import com.kaoyan.assistant.application.school.dto.MajorResponse;
import com.kaoyan.assistant.application.school.dto.SchoolDetailResponse;
import com.kaoyan.assistant.application.school.dto.SchoolSummaryResponse;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.Major;
import com.kaoyan.assistant.domain.entity.School;
import com.kaoyan.assistant.infrastructure.repository.AdmissionGuideRepository;
import com.kaoyan.assistant.infrastructure.repository.ApplicationRatioRepository;
import com.kaoyan.assistant.infrastructure.repository.MajorRepository;
import com.kaoyan.assistant.infrastructure.repository.SchoolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SchoolQueryService {

    private final SchoolRepository schoolRepository;
    private final MajorRepository majorRepository;
    private final AdmissionGuideRepository admissionGuideRepository;
    private final ApplicationRatioRepository applicationRatioRepository;

    public SchoolQueryService(SchoolRepository schoolRepository,
                              MajorRepository majorRepository,
                              AdmissionGuideRepository admissionGuideRepository,
                              ApplicationRatioRepository applicationRatioRepository) {
        this.schoolRepository = schoolRepository;
        this.majorRepository = majorRepository;
        this.admissionGuideRepository = admissionGuideRepository;
        this.applicationRatioRepository = applicationRatioRepository;
    }

    public List<SchoolSummaryResponse> listSchools(String keyword) {
        List<School> schools = keyword == null || keyword.isBlank()
                ? schoolRepository.findAll()
                : schoolRepository.findBySchoolNameContainingIgnoreCaseOrderByIdAsc(keyword.trim());
        return schools
                .stream()
                .map(this::toSchoolSummary)
                .toList();
    }

    public SchoolDetailResponse getSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new BusinessException("school not found"));
        List<MajorResponse> majors = majorRepository.findBySchoolIdOrderByIdAsc(id)
                .stream()
                .map(major -> new MajorResponse(
                        major.getId(),
                        major.getSchoolId(),
                        major.getMajorName(),
                        major.getMajorCode(),
                        major.getDegreeType(),
                        major.getDescription()
                ))
                .toList();
        Map<Long, String> majorNames = majorRepository.findBySchoolIdOrderByIdAsc(id)
                .stream()
                .collect(Collectors.toMap(Major::getId, Major::getMajorName));
        List<AdmissionGuideResponse> guides = admissionGuideRepository.findBySchoolIdOrderByGuideYearDescIdDesc(id)
                .stream()
                .map(guide -> new AdmissionGuideResponse(
                        guide.getId(),
                        guide.getSchoolId(),
                        school.getSchoolName(),
                        guide.getMajorId(),
                        majorNames.getOrDefault(guide.getMajorId(), "全部专业"),
                        guide.getTitle(),
                        guide.getContent(),
                        guide.getGuideYear()
                ))
                .toList();
        List<ApplicationRatioResponse> ratios = applicationRatioRepository.findBySchoolIdOrderByRatioYearDescIdDesc(id)
                .stream()
                .map(ratio -> new ApplicationRatioResponse(
                        ratio.getId(),
                        ratio.getSchoolId(),
                        school.getSchoolName(),
                        ratio.getMajorId(),
                        majorNames.getOrDefault(ratio.getMajorId(), "未知专业"),
                        ratio.getRatioYear(),
                        ratio.getApplyCount(),
                        ratio.getAdmitCount(),
                        ratio.getRatioValue()
                ))
                .toList();
        return new SchoolDetailResponse(
                school.getId(),
                school.getSchoolName(),
                school.getProvince(),
                school.getCity(),
                school.getSchoolType(),
                school.getSchoolLevel(),
                school.getDescription(),
                majors,
                guides,
                ratios
        );
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
}
