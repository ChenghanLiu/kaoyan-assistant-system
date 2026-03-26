package com.kaoyan.assistant.application.system;

import com.kaoyan.assistant.application.system.dto.SystemConfigResponse;
import com.kaoyan.assistant.application.system.dto.SystemConfigUpdateRequest;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.domain.entity.SystemConfig;
import com.kaoyan.assistant.infrastructure.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;
    private final OperationLogService operationLogService;

    public SystemConfigService(SystemConfigRepository systemConfigRepository,
                               OperationLogService operationLogService) {
        this.systemConfigRepository = systemConfigRepository;
        this.operationLogService = operationLogService;
    }

    @Transactional(readOnly = true)
    public List<SystemConfigResponse> listConfigs() {
        return systemConfigRepository.findAllByOrderByConfigKeyAsc()
                .stream()
                .map(config -> new SystemConfigResponse(
                        config.getId(),
                        config.getConfigKey(),
                        config.getConfigValue(),
                        config.getConfigDescription(),
                        config.getUpdatedAt()
                ))
                .toList();
    }

    @Transactional
    public SystemConfigResponse updateConfig(LoginUser loginUser, String configKey, SystemConfigUpdateRequest request) {
        SystemConfig config = systemConfigRepository.findByConfigKey(configKey)
                .orElseThrow(() -> BusinessException.notFound("system config not found"));
        String configValue = request.configValue().trim();
        if (!StringUtils.hasText(configValue)) {
            throw BusinessException.invalidInput("configValue must not be blank");
        }
        config.setConfigValue(configValue);
        config.setConfigDescription(StringUtils.hasText(request.configDescription()) ? request.configDescription().trim() : null);
        SystemConfig savedConfig = systemConfigRepository.save(config);
        operationLogService.record(loginUser, "CONFIG", "UPDATE",
                "修改系统配置 " + savedConfig.getConfigKey(), "/api/admin/configs/" + savedConfig.getConfigKey());
        return new SystemConfigResponse(
                savedConfig.getId(),
                savedConfig.getConfigKey(),
                savedConfig.getConfigValue(),
                savedConfig.getConfigDescription(),
                savedConfig.getUpdatedAt()
        );
    }
}
