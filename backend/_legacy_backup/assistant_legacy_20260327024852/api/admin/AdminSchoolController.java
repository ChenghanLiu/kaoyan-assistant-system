package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.domain.entity.AdmissionGuide;
import com.kaoyan.assistant.domain.entity.Major;
import com.kaoyan.assistant.domain.entity.School;
import com.kaoyan.assistant.infrastructure.repository.AdmissionGuideRepository;
import com.kaoyan.assistant.infrastructure.repository.MajorRepository;
import com.kaoyan.assistant.infrastructure.repository.SchoolRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminSchoolController {

    private final SchoolRepository schoolRepository;
    private final MajorRepository majorRepository;
    private final AdmissionGuideRepository admissionGuideRepository;

    @GetMapping("/schools")
    public ApiResponse<?> listSchools() {
        return ApiResponse.success(schoolRepository.findAll());
    }

    @PostMapping("/schools")
    public ApiResponse<?> createSchool(@Valid @RequestBody SchoolRequest request) {
        School school = new School();
        school.setSchoolName(request.schoolName());
        school.setCity(request.city());
        school.setProvince(request.province());
        school.setSchoolType(request.schoolType());
        school.setSchoolLevel(request.schoolLevel());
        school.setDescription(request.description());
        return ApiResponse.success("Create success", schoolRepository.save(school));
    }

    @PutMapping("/schools/{schoolId}")
    public ApiResponse<?> updateSchool(@PathVariable Long schoolId, @Valid @RequestBody SchoolRequest request) {
        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new BusinessException("School not found"));
        school.setSchoolName(request.schoolName());
        school.setCity(request.city());
        school.setProvince(request.province());
        school.setSchoolType(request.schoolType());
        school.setSchoolLevel(request.schoolLevel());
        school.setDescription(request.description());
        return ApiResponse.success("Update success", schoolRepository.save(school));
    }

    @DeleteMapping("/schools/{schoolId}")
    public ApiResponse<?> deleteSchool(@PathVariable Long schoolId) {
        schoolRepository.deleteById(schoolId);
        return ApiResponse.success("Delete success", null);
    }

    @GetMapping("/majors")
    public ApiResponse<?> listMajors() {
        return ApiResponse.success(majorRepository.findAll());
    }

    @PostMapping("/majors")
    public ApiResponse<?> createMajor(@Valid @RequestBody MajorRequest request) {
        Major major = new Major();
        major.setMajorName(request.majorName());
        major.setMajorCode(request.majorCode());
        major.setCategoryName(request.categoryName());
        major.setDescription(request.description());
        return ApiResponse.success("Create success", majorRepository.save(major));
    }

    @GetMapping("/guides")
    public ApiResponse<?> listGuides() {
        return ApiResponse.success(admissionGuideRepository.findAll());
    }

    @PostMapping("/guides")
    public ApiResponse<?> createGuide(@Valid @RequestBody GuideRequest request) {
        AdmissionGuide guide = new AdmissionGuide();
        guide.setSchoolId(request.schoolId());
        guide.setMajorId(request.majorId());
        guide.setTitle(request.title());
        guide.setContent(request.content());
        guide.setGuideYear(request.guideYear());
        return ApiResponse.success("Create success", admissionGuideRepository.save(guide));
    }

    public record SchoolRequest(@NotBlank String schoolName, @NotBlank String city, @NotBlank String province,
                                @NotBlank String schoolType, String schoolLevel, String description) {
    }

    public record MajorRequest(@NotBlank String majorName, @NotBlank String majorCode,
                               @NotBlank String categoryName, String description) {
    }

    public record GuideRequest(Long schoolId, Long majorId, @NotBlank String title, String content, Integer guideYear) {
    }
}
