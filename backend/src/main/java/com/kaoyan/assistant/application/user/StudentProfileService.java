package com.kaoyan.assistant.application.user;

import com.kaoyan.assistant.application.auth.dto.UserProfileResponse;
import com.kaoyan.assistant.application.user.dto.StudentProfileUpdateRequest;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentProfileService {

    private final SysUserRepository userRepository;

    public StudentProfileService(SysUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        return toResponse(getUser(userId));
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, StudentProfileUpdateRequest request) {
        SysUser user = getUser(userId);
        String displayName = normalizeRequired(request.displayName(), "displayName is required");

        user.setDisplayName(displayName);
        user.setTargetSchool(normalizeOptional(request.targetSchool()));
        user.setTargetMajor(normalizeOptional(request.targetMajor()));

        return toResponse(userRepository.save(user));
    }

    private SysUser getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("user not found"));
    }

    private UserProfileResponse toResponse(SysUser user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhone(),
                user.getTargetSchool(),
                user.getTargetMajor(),
                user.getRoles().stream().map(role -> role.getRoleCode().name()).toList()
        );
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            throw new BusinessException(message);
        }
        return normalized;
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
