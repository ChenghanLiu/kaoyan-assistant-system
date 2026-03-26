package com.kaoyan.assistant.common.security;

import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserRepository userRepository;

    public CustomUserDetailsService(SysUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(LoginUser::new)
                .orElseThrow(() -> new BusinessException("User not found"));
    }
}
