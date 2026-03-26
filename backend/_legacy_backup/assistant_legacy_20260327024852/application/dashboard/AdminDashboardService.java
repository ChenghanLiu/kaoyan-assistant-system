package com.kaoyan.assistant.application.dashboard;

import com.kaoyan.assistant.infrastructure.repository.ExamPaperRepository;
import com.kaoyan.assistant.infrastructure.repository.MaterialRepository;
import com.kaoyan.assistant.infrastructure.repository.PostRepository;
import com.kaoyan.assistant.infrastructure.repository.SchoolRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final SysUserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final PostRepository postRepository;
    private final MaterialRepository materialRepository;
    private final ExamPaperRepository examPaperRepository;

    public Map<String, Object> getDashboard() {
        return Map.of(
                "userCount", userRepository.count(),
                "schoolCount", schoolRepository.count(),
                "postCount", postRepository.count(),
                "materialCount", materialRepository.count(),
                "paperCount", examPaperRepository.count()
        );
    }
}
