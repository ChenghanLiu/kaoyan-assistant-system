package com.kaoyan.assistant.application.user;

import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final SysUserRepository userRepository;

    public List<SysUser> listUsers() {
        return userRepository.findAll();
    }

    public SysUser changeEnabled(Long userId, Boolean enabled) {
        SysUser user = userRepository.findById(userId).orElseThrow(() -> new BusinessException("User not found"));
        user.setEnabled(enabled);
        return userRepository.save(user);
    }
}
