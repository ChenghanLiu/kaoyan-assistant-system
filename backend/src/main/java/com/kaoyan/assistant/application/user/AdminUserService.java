package com.kaoyan.assistant.application.user;

import com.kaoyan.assistant.application.user.dto.AdminUserResponse;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    private final SysUserRepository userRepository;

    public AdminUserService(SysUserRepository userRepository) {
        this.userRepository = userRepository;
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
}
