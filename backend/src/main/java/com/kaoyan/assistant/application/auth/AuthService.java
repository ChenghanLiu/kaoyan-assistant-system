package com.kaoyan.assistant.application.auth;

import com.kaoyan.assistant.application.auth.dto.AuthResponse;
import com.kaoyan.assistant.application.auth.dto.LoginRequest;
import com.kaoyan.assistant.application.auth.dto.RegisterRequest;
import com.kaoyan.assistant.application.auth.dto.UserProfileResponse;
import com.kaoyan.assistant.application.system.OperationLogService;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.common.security.JwtTokenProvider;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.domain.entity.SysRole;
import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.domain.enums.RoleCode;
import com.kaoyan.assistant.infrastructure.repository.SysRoleRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final OperationLogService operationLogService;

    public AuthService(SysUserRepository userRepository,
                       SysRoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       OperationLogService operationLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.operationLogService = operationLogService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("username already exists");
        }
        SysRole studentRole = roleRepository.findByRoleCode(RoleCode.STUDENT)
                .orElseThrow(() -> new BusinessException("student role not found"));

        SysUser user = new SysUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setTargetSchool(request.targetSchool());
        user.setTargetMajor(request.targetMajor());
        user.setEnabled(true);
        user.getRoles().add(studentRole);

        SysUser savedUser = userRepository.save(user);
        LoginUser loginUser = new LoginUser(savedUser);
        operationLogService.record(loginUser, "AUTH", "REGISTER",
                "用户完成注册: " + savedUser.getUsername(), "/api/auth/register");
        return buildAuthResponse(loginUser);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            operationLogService.record(loginUser, "AUTH", "LOGIN",
                    "用户登录系统: " + loginUser.getUsername(), "/api/auth/login");
            return buildAuthResponse(loginUser);
        } catch (AuthenticationException exception) {
            throw new BusinessException("username or password is invalid");
        }
    }

    public UserProfileResponse me(LoginUser loginUser) {
        if (loginUser == null) {
            throw new BusinessException("user is not authenticated");
        }
        SysUser user = userRepository.findByUsername(loginUser.getUsername())
                .orElseThrow(() -> new BusinessException("user not found"));
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

    private AuthResponse buildAuthResponse(LoginUser loginUser) {
        return new AuthResponse(jwtTokenProvider.createToken(loginUser), me(loginUser));
    }
}
