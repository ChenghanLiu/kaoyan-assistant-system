package com.kaoyan.assistant.application.dashboard;

import com.kaoyan.assistant.application.content.ContentQueryService;
import com.kaoyan.assistant.application.dashboard.dto.StudentHomeSummaryResponse;
import com.kaoyan.assistant.infrastructure.repository.ExamPaperRepository;
import com.kaoyan.assistant.infrastructure.repository.MajorRepository;
import com.kaoyan.assistant.infrastructure.repository.MaterialRepository;
import com.kaoyan.assistant.infrastructure.repository.PostRepository;
import com.kaoyan.assistant.infrastructure.repository.SchoolRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentDashboardService {

    private final SchoolRepository schoolRepository;
    private final MajorRepository majorRepository;
    private final MaterialRepository materialRepository;
    private final PostRepository postRepository;
    private final ExamPaperRepository examPaperRepository;
    private final ContentQueryService contentQueryService;

    public StudentDashboardService(SchoolRepository schoolRepository,
                                   MajorRepository majorRepository,
                                   MaterialRepository materialRepository,
                                   PostRepository postRepository,
                                   ExamPaperRepository examPaperRepository,
                                   ContentQueryService contentQueryService) {
        this.schoolRepository = schoolRepository;
        this.majorRepository = majorRepository;
        this.materialRepository = materialRepository;
        this.postRepository = postRepository;
        this.examPaperRepository = examPaperRepository;
        this.contentQueryService = contentQueryService;
    }

    public StudentHomeSummaryResponse getSummary() {
        return new StudentHomeSummaryResponse(
                schoolRepository.count(),
                majorRepository.count(),
                materialRepository.count(),
                postRepository.count(),
                examPaperRepository.count(),
                contentQueryService.listApprovedMaterials().stream().limit(3).toList(),
                contentQueryService.listPosts().stream().limit(3).toList()
        );
    }
}
