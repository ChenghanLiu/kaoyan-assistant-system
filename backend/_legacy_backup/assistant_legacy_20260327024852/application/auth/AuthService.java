package com.kaoyan.assistant.application.auth;

import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.common.security.JwtTokenProvider;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.domain.entity.SysRole;
import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.domain.enums.RoleCode;
import com.kaoyan.assistant.infrastructure.repository.SysRoleRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import com.kaoyan.assistant.application.user.OperationLogService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Validated
@RequiredArgsConstructor
public class AuthService {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final OperationLogService operationLogService;

    public Map<String, Object> register(RegisterCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            throw new BusinessException("Username already exists");
        }
        SysRole studentRole = roleRepository.findByRoleCode(RoleCode.STUDENT)
                .orElseThrow(() -> new BusinessException("Student role not found"));
        SysUser user = new SysUser();
        user.setUsername(command.username());
        user.setPassword(passwordEncoder.encode(command.password()));
        user.setRealName(command.realName());
        user.setEmail(command.email());
        user.setPhone(command.phone());
        user.setTargetSchool(command.targetSchool());
        user.setTargetMajor(command.targetMajor());
        user.getRoles().add(studentRole);
        user = userRepository.save(user);
        operationLogService.save(user, "REGISTER", "Student account registered");
        LoginUser loginUser = new LoginUser(user);
        return buildAuthResult(loginUser);
    }

    public Map<String, Object> login(LoginCommand command) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(command.username(), command.password()));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        operationLogService.save(loginUser.getUser(), "LOGIN", "User login");
        return buildAuthResult(loginUser);
    }

    public Map<String, Object> me(LoginUser loginUser) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", loginUser.getId());
        result.put("username", loginUser.getUsername());
        result.put("realName", loginUser.getRealName());
        result.put("email", loginUser.getUser().getEmail());
        result.put("phone", loginUser.getUser().getPhone());
        result.put("targetSchool", loginUser.getUser().getTargetSchool());
        result.put("targetMajor", loginUser.getUser().getTargetMajor());
        result.put("roles", loginUser.getRoleCodes());
        return result;
    }

    private Map<String, Object> buildAuthResult(LoginUser loginUser) {
        return Map.of(
                "token", jwtTokenProvider.createToken(loginUser),
                "user", me(loginUser)
        );
    }

    public record RegisterCommand(
            @NotBlank @Size(min = 4, max = 64) String username,
            @NotBlank @Size(min = 6, max = 32) String password,
            @NotBlank String realName,
            String email,
            String phone,
            String targetSchool,
            String targetMajor
    ) {
    }

    public record LoginCommand(@NotBlank String username, @NotBlank String password) {
    }
}
