package com.kaoyan.assistant.application.major;

import com.kaoyan.assistant.application.school.dto.MajorResponse;
import com.kaoyan.assistant.infrastructure.repository.MajorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorQueryService {

    private final MajorRepository majorRepository;

    public MajorQueryService(MajorRepository majorRepository) {
        this.majorRepository = majorRepository;
    }

    public List<MajorResponse> listMajors() {
        return majorRepository.findAllByOrderByIdAsc()
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
    }
}
