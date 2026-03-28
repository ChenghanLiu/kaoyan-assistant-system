package com.kaoyan.assistant.application.user;

import com.kaoyan.assistant.application.user.dto.AdminUserResponse;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.domain.enums.RoleCode;
import com.kaoyan.assistant.infrastructure.repository.ExamRecordRepository;
import com.kaoyan.assistant.infrastructure.repository.MaterialRepository;
import com.kaoyan.assistant.infrastructure.repository.OperationLogRepository;
import com.kaoyan.assistant.infrastructure.repository.PostCommentRepository;
import com.kaoyan.assistant.infrastructure.repository.PostRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyPlanRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyProgressRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyReminderRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyTaskRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import com.kaoyan.assistant.infrastructure.repository.WrongQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserService {

    private final SysUserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final ExamRecordRepository examRecordRepository;
    private final WrongQuestionRepository wrongQuestionRepository;
    private final StudyPlanRepository studyPlanRepository;
    private final StudyTaskRepository studyTaskRepository;
    private final StudyProgressRepository studyProgressRepository;
    private final StudyReminderRepository studyReminderRepository;
    private final OperationLogRepository operationLogRepository;

    public AdminUserService(SysUserRepository userRepository,
                            MaterialRepository materialRepository,
                            PostRepository postRepository,
                            PostCommentRepository postCommentRepository,
                            ExamRecordRepository examRecordRepository,
                            WrongQuestionRepository wrongQuestionRepository,
                            StudyPlanRepository studyPlanRepository,
                            StudyTaskRepository studyTaskRepository,
                            StudyProgressRepository studyProgressRepository,
                            StudyReminderRepository studyReminderRepository,
                            OperationLogRepository operationLogRepository) {
        this.userRepository = userRepository;
        this.materialRepository = materialRepository;
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.examRecordRepository = examRecordRepository;
        this.wrongQuestionRepository = wrongQuestionRepository;
        this.studyPlanRepository = studyPlanRepository;
        this.studyTaskRepository = studyTaskRepository;
        this.studyProgressRepository = studyProgressRepository;
        this.studyReminderRepository = studyReminderRepository;
        this.operationLogRepository = operationLogRepository;
    }

    public List<AdminUserResponse> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getDisplayName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.isEnabled(),
                        user.getTargetSchool(),
                        user.getTargetMajor(),
                        user.getRoles().stream().map(role -> role.getRoleCode().name()).toList(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void deleteUser(Long userId, Long currentAdminId) {
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("user not found"));
        if (user.getId().equals(currentAdminId)) {
            throw BusinessException.invalidInput("cannot delete current login user");
        }
        if (user.getRoles().stream().anyMatch(role -> role.getRoleCode() == RoleCode.ADMIN)
                && userRepository.countDistinctByRoleCode(RoleCode.ADMIN) <= 1) {
            throw BusinessException.invalidInput("cannot delete the last admin user");
        }
        if (hasRelatedData(userId)) {
            throw BusinessException.invalidInput("user has related business data and cannot be deleted");
        }
        userRepository.delete(user);
    }

    private boolean hasRelatedData(Long userId) {
        return materialRepository.existsByUserId(userId)
                || materialRepository.existsByReviewerId(userId)
                || postRepository.existsByUserId(userId)
                || postCommentRepository.existsByUserId(userId)
                || examRecordRepository.existsByUserId(userId)
                || wrongQuestionRepository.existsByUserId(userId)
                || studyPlanRepository.existsByUserId(userId)
                || studyTaskRepository.existsByUserId(userId)
                || studyProgressRepository.existsByUserId(userId)
                || studyReminderRepository.existsByUserId(userId)
                || operationLogRepository.existsByUserId(userId);
    }
}
