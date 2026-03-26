package com.kaoyan.assistant.application.school;

import com.kaoyan.assistant.application.school.dto.AdmissionGuideResponse;
import com.kaoyan.assistant.application.school.dto.ApplicationRatioResponse;
import com.kaoyan.assistant.application.school.dto.PolicyNewsResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SchoolInformationService {

    private final AdmissionGuideRepository admissionGuideRepository;
    private final ApplicationRatioRepository applicationRatioRepository;
    private final PolicyNewsRepository policyNewsRepository;
    private final SchoolRepository schoolRepository;
    private final MajorRepository majorRepository;

    public SchoolInformationService(AdmissionGuideRepository admissionGuideRepository,
                                    ApplicationRatioRepository applicationRatioRepository,
                                    PolicyNewsRepository policyNewsRepository,
                                    SchoolRepository schoolRepository,
                                    MajorRepository majorRepository) {
        this.admissionGuideRepository = admissionGuideRepository;
        this.applicationRatioRepository = applicationRatioRepository;
        this.policyNewsRepository = policyNewsRepository;
        this.schoolRepository = schoolRepository;
        this.majorRepository = majorRepository;
    }

    @Transactional(readOnly = true)
    public List<AdmissionGuideResponse> listAdmissions(Long schoolId, Long majorId) {
        ReferenceMaps references = loadReferences();
        return admissionGuideRepository.findAllByFilter(schoolId, majorId)
                .stream()
                .map(guide -> toAdmissionGuideResponse(guide, references))
                .toList();
    }

    @Transactional(readOnly = true)
    public AdmissionGuideResponse getAdmission(Long admissionId) {
        ReferenceMaps references = loadReferences();
        AdmissionGuide guide = admissionGuideRepository.findById(admissionId)
                .orElseThrow(() -> BusinessException.notFound("admission guide not found"));
        return toAdmissionGuideResponse(guide, references);
    }

    @Transactional(readOnly = true)
    public List<ApplicationRatioResponse> listRatios(Long schoolId, Long majorId) {
        ReferenceMaps references = loadReferences();
        return applicationRatioRepository.findAllByFilter(schoolId, majorId)
                .stream()
                .map(ratio -> toApplicationRatioResponse(ratio, references))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PolicyNewsResponse> listPolicies(Long schoolId, Long majorId) {
        ReferenceMaps references = loadReferences();
        return policyNewsRepository.findAllByFilter(schoolId, majorId)
                .stream()
                .map(news -> toPolicyNewsResponse(news, references))
                .toList();
    }

    private AdmissionGuideResponse toAdmissionGuideResponse(AdmissionGuide guide, ReferenceMaps references) {
        return new AdmissionGuideResponse(
                guide.getId(),
                guide.getSchoolId(),
                references.schoolNames().getOrDefault(guide.getSchoolId(), "未知院校"),
                guide.getMajorId(),
                references.majorNames().getOrDefault(guide.getMajorId(), "全部专业"),
                guide.getTitle(),
                guide.getContent(),
                guide.getGuideYear()
        );
    }

    private ApplicationRatioResponse toApplicationRatioResponse(ApplicationRatio ratio, ReferenceMaps references) {
        return new ApplicationRatioResponse(
                ratio.getId(),
                ratio.getSchoolId(),
                references.schoolNames().getOrDefault(ratio.getSchoolId(), "未知院校"),
                ratio.getMajorId(),
                references.majorNames().getOrDefault(ratio.getMajorId(), "未知专业"),
                ratio.getRatioYear(),
                ratio.getApplyCount(),
                ratio.getAdmitCount(),
                ratio.getRatioValue()
        );
    }

    private PolicyNewsResponse toPolicyNewsResponse(PolicyNews news, ReferenceMaps references) {
        return new PolicyNewsResponse(
                news.getId(),
                news.getSchoolId(),
                references.schoolNames().getOrDefault(news.getSchoolId(), "全部院校"),
                news.getMajorId(),
                references.majorNames().getOrDefault(news.getMajorId(), "全部专业"),
                news.getTitle(),
                news.getContent(),
                news.getSourceName(),
                news.getPublishedDate()
        );
    }

    private ReferenceMaps loadReferences() {
        Map<Long, String> schoolNames = schoolRepository.findAll()
                .stream()
                .collect(Collectors.toMap(School::getId, School::getSchoolName));
        Map<Long, String> majorNames = majorRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Major::getId, Major::getMajorName, (left, right) -> left));
        return new ReferenceMaps(schoolNames, majorNames);
    }

    private record ReferenceMaps(Map<Long, String> schoolNames, Map<Long, String> majorNames) {
    }
}
