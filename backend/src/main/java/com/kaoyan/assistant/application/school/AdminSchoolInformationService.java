package com.kaoyan.assistant.application.school;

import com.kaoyan.assistant.application.school.dto.AdmissionGuideCreateRequest;
import com.kaoyan.assistant.application.school.dto.AdmissionGuideResponse;
import com.kaoyan.assistant.application.school.dto.AdmissionGuideUpdateRequest;
import com.kaoyan.assistant.application.school.dto.ApplicationRatioCreateRequest;
import com.kaoyan.assistant.application.school.dto.ApplicationRatioResponse;
import com.kaoyan.assistant.application.school.dto.ApplicationRatioUpdateRequest;
import com.kaoyan.assistant.application.school.dto.PolicyNewsCreateRequest;
import com.kaoyan.assistant.application.school.dto.PolicyNewsResponse;
import com.kaoyan.assistant.application.school.dto.PolicyNewsUpdateRequest;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.AdmissionGuide;
import com.kaoyan.assistant.domain.entity.ApplicationRatio;
import com.kaoyan.assistant.domain.entity.PolicyNews;
import com.kaoyan.assistant.infrastructure.repository.AdmissionGuideRepository;
import com.kaoyan.assistant.infrastructure.repository.ApplicationRatioRepository;
import com.kaoyan.assistant.infrastructure.repository.MajorRepository;
import com.kaoyan.assistant.infrastructure.repository.PolicyNewsRepository;
import com.kaoyan.assistant.infrastructure.repository.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminSchoolInformationService {

    private final AdmissionGuideRepository admissionGuideRepository;
    private final ApplicationRatioRepository applicationRatioRepository;
    private final PolicyNewsRepository policyNewsRepository;
    private final SchoolRepository schoolRepository;
    private final MajorRepository majorRepository;
    private final SchoolInformationService schoolInformationService;

    public AdminSchoolInformationService(AdmissionGuideRepository admissionGuideRepository,
                                         ApplicationRatioRepository applicationRatioRepository,
                                         PolicyNewsRepository policyNewsRepository,
                                         SchoolRepository schoolRepository,
                                         MajorRepository majorRepository,
                                         SchoolInformationService schoolInformationService) {
        this.admissionGuideRepository = admissionGuideRepository;
        this.applicationRatioRepository = applicationRatioRepository;
        this.policyNewsRepository = policyNewsRepository;
        this.schoolRepository = schoolRepository;
        this.majorRepository = majorRepository;
        this.schoolInformationService = schoolInformationService;
    }

    @Transactional(readOnly = true)
    public List<AdmissionGuideResponse> listAdmissions(Long schoolId, Long majorId) {
        return schoolInformationService.listAdmissions(schoolId, majorId);
    }

    @Transactional
    public AdmissionGuideResponse createAdmission(AdmissionGuideCreateRequest request) {
        validateSchoolAndMajor(request.schoolId(), request.majorId());
        AdmissionGuide guide = new AdmissionGuide();
        guide.setSchoolId(request.schoolId());
        guide.setMajorId(request.majorId());
        guide.setTitle(request.title().trim());
        guide.setContent(request.content().trim());
        guide.setGuideYear(request.guideYear());
        AdmissionGuide saved = admissionGuideRepository.save(guide);
        return schoolInformationService.getAdmission(saved.getId());
    }

    @Transactional
    public AdmissionGuideResponse updateAdmission(Long admissionId, AdmissionGuideUpdateRequest request) {
        AdmissionGuide guide = admissionGuideRepository.findById(admissionId)
                .orElseThrow(() -> BusinessException.notFound("admission guide not found"));
        Long schoolId = request.schoolId() != null ? request.schoolId() : guide.getSchoolId();
        Long majorId = request.majorId() != null ? request.majorId() : guide.getMajorId();
        validateSchoolAndMajor(schoolId, majorId);
        guide.setSchoolId(schoolId);
        guide.setMajorId(majorId);
        if (request.title() != null) {
            guide.setTitle(request.title().trim());
        }
        if (request.content() != null) {
            guide.setContent(request.content().trim());
        }
        if (request.guideYear() != null) {
            guide.setGuideYear(request.guideYear());
        }
        AdmissionGuide saved = admissionGuideRepository.save(guide);
        return schoolInformationService.getAdmission(saved.getId());
    }

    @Transactional
    public void deleteAdmission(Long admissionId) {
        AdmissionGuide guide = admissionGuideRepository.findById(admissionId)
                .orElseThrow(() -> BusinessException.notFound("admission guide not found"));
        admissionGuideRepository.delete(guide);
    }

    @Transactional(readOnly = true)
    public List<ApplicationRatioResponse> listRatios(Long schoolId, Long majorId) {
        return schoolInformationService.listRatios(schoolId, majorId);
    }

    @Transactional
    public ApplicationRatioResponse createRatio(ApplicationRatioCreateRequest request) {
        validateSchoolAndMajor(request.schoolId(), request.majorId());
        ApplicationRatio ratio = new ApplicationRatio();
        ratio.setSchoolId(request.schoolId());
        ratio.setMajorId(request.majorId());
        ratio.setRatioYear(request.ratioYear());
        ratio.setApplyCount(request.applyCount());
        ratio.setAdmitCount(request.admitCount());
        ratio.setRatioValue(request.ratioValue().trim());
        ApplicationRatio saved = applicationRatioRepository.save(ratio);
        return schoolInformationService.listRatios(saved.getSchoolId(), saved.getMajorId())
                .stream()
                .filter(item -> item.id().equals(saved.getId()))
                .findFirst()
                .orElseThrow(() -> BusinessException.notFound("application ratio not found"));
    }

    @Transactional
    public ApplicationRatioResponse updateRatio(Long ratioId, ApplicationRatioUpdateRequest request) {
        ApplicationRatio ratio = applicationRatioRepository.findById(ratioId)
                .orElseThrow(() -> BusinessException.notFound("application ratio not found"));
        Long schoolId = request.schoolId() != null ? request.schoolId() : ratio.getSchoolId();
        Long majorId = request.majorId() != null ? request.majorId() : ratio.getMajorId();
        validateSchoolAndMajor(schoolId, majorId);
        ratio.setSchoolId(schoolId);
        ratio.setMajorId(majorId);
        if (request.ratioYear() != null) {
            ratio.setRatioYear(request.ratioYear());
        }
        if (request.applyCount() != null) {
            ratio.setApplyCount(request.applyCount());
        }
        if (request.admitCount() != null) {
            ratio.setAdmitCount(request.admitCount());
        }
        if (request.ratioValue() != null) {
            ratio.setRatioValue(request.ratioValue().trim());
        }
        ApplicationRatio saved = applicationRatioRepository.save(ratio);
        return schoolInformationService.listRatios(saved.getSchoolId(), saved.getMajorId())
                .stream()
                .filter(item -> item.id().equals(saved.getId()))
                .findFirst()
                .orElseThrow(() -> BusinessException.notFound("application ratio not found"));
    }

    @Transactional
    public void deleteRatio(Long ratioId) {
        ApplicationRatio ratio = applicationRatioRepository.findById(ratioId)
                .orElseThrow(() -> BusinessException.notFound("application ratio not found"));
        applicationRatioRepository.delete(ratio);
    }

    @Transactional(readOnly = true)
    public List<PolicyNewsResponse> listPolicies(Long schoolId, Long majorId) {
        return schoolInformationService.listPolicies(schoolId, majorId);
    }

    @Transactional
    public PolicyNewsResponse createPolicy(PolicyNewsCreateRequest request) {
        validateSchoolAndMajor(request.schoolId(), request.majorId());
        PolicyNews news = new PolicyNews();
        news.setSchoolId(request.schoolId());
        news.setMajorId(request.majorId());
        news.setTitle(request.title().trim());
        news.setContent(request.content().trim());
        news.setSourceName(request.sourceName() == null ? null : request.sourceName().trim());
        news.setPublishedDate(request.publishedDate());
        PolicyNews saved = policyNewsRepository.save(news);
        return schoolInformationService.listPolicies(saved.getSchoolId(), saved.getMajorId())
                .stream()
                .filter(item -> item.id().equals(saved.getId()))
                .findFirst()
                .orElseThrow(() -> BusinessException.notFound("policy news not found"));
    }

    @Transactional
    public PolicyNewsResponse updatePolicy(Long policyId, PolicyNewsUpdateRequest request) {
        PolicyNews news = policyNewsRepository.findById(policyId)
                .orElseThrow(() -> BusinessException.notFound("policy news not found"));
        Long schoolId = request.schoolId() != null ? request.schoolId() : news.getSchoolId();
        Long majorId = request.majorId() != null ? request.majorId() : news.getMajorId();
        validateSchoolAndMajor(schoolId, majorId);
        news.setSchoolId(schoolId);
        news.setMajorId(majorId);
        if (request.title() != null) {
            news.setTitle(request.title().trim());
        }
        if (request.content() != null) {
            news.setContent(request.content().trim());
        }
        if (request.sourceName() != null) {
            news.setSourceName(request.sourceName().trim());
        }
        if (request.publishedDate() != null) {
            news.setPublishedDate(request.publishedDate());
        }
        PolicyNews saved = policyNewsRepository.save(news);
        return schoolInformationService.listPolicies(saved.getSchoolId(), saved.getMajorId())
                .stream()
                .filter(item -> item.id().equals(saved.getId()))
                .findFirst()
                .orElseThrow(() -> BusinessException.notFound("policy news not found"));
    }

    @Transactional
    public void deletePolicy(Long policyId) {
        PolicyNews news = policyNewsRepository.findById(policyId)
                .orElseThrow(() -> BusinessException.notFound("policy news not found"));
        policyNewsRepository.delete(news);
    }

    private void validateSchoolAndMajor(Long schoolId, Long majorId) {
        if (schoolId != null && !schoolRepository.existsById(schoolId)) {
            throw BusinessException.notFound("school not found");
        }
        if (majorId != null) {
            var major = majorRepository.findById(majorId)
                    .orElseThrow(() -> BusinessException.notFound("major not found"));
            if (schoolId != null && !major.getSchoolId().equals(schoolId)) {
                throw BusinessException.invalidInput("major does not belong to school");
            }
        }
    }
}
