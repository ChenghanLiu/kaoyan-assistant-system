package com.kaoyan.assistant.application.dashboard;

import com.kaoyan.assistant.application.dashboard.dto.AdminDashboardSummaryResponse;
import com.kaoyan.assistant.domain.enums.MaterialStatus;
import com.kaoyan.assistant.infrastructure.repository.ExamPaperRepository;
import com.kaoyan.assistant.infrastructure.repository.MajorRepository;
import com.kaoyan.assistant.infrastructure.repository.MaterialRepository;
import com.kaoyan.assistant.infrastructure.repository.PostRepository;
import com.kaoyan.assistant.infrastructure.repository.SchoolRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    private final SysUserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final MajorRepository majorRepository;
    private final MaterialRepository materialRepository;
    private final PostRepository postRepository;
    private final ExamPaperRepository examPaperRepository;

    public AdminDashboardService(SysUserRepository userRepository,
                                 SchoolRepository schoolRepository,
                                 MajorRepository majorRepository,
                                 MaterialRepository materialRepository,
                                 PostRepository postRepository,
                                 ExamPaperRepository examPaperRepository) {
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
        this.majorRepository = majorRepository;
        this.materialRepository = materialRepository;
        this.postRepository = postRepository;
        this.examPaperRepository = examPaperRepository;
    }

    public AdminDashboardSummaryResponse getSummary() {
        return new AdminDashboardSummaryResponse(
                userRepository.count(),
                schoolRepository.count(),
                majorRepository.count(),
                materialRepository.count(),
                materialRepository.countByReviewStatus(MaterialStatus.PENDING),
                postRepository.count(),
                examPaperRepository.count()
        );
    }
}
