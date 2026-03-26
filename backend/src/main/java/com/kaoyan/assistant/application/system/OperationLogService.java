package com.kaoyan.assistant.application.system;

import com.kaoyan.assistant.application.system.dto.OperationLogResponse;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.domain.entity.OperationLog;
import com.kaoyan.assistant.infrastructure.repository.OperationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    public OperationLogService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Transactional
    public void record(LoginUser loginUser, String module, String type, String content, String requestPath) {
        OperationLog operationLog = new OperationLog();
        if (loginUser != null) {
            operationLog.setUserId(loginUser.getId());
            operationLog.setUsername(loginUser.getUsername());
            operationLog.setUserRole(loginUser.getAuthorities().stream()
                    .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                    .sorted()
                    .findFirst()
                    .orElse(null));
        }
        operationLog.setOperationModule(module);
        operationLog.setOperationType(type);
        operationLog.setOperationContent(content);
        operationLog.setRequestPath(requestPath);
        operationLogRepository.save(operationLog);
    }

    @Transactional(readOnly = true)
    public List<OperationLogResponse> listLogs() {
        return operationLogRepository.findTop200ByOrderByCreatedAtDescIdDesc()
                .stream()
                .map(log -> new OperationLogResponse(
                        log.getId(),
                        log.getUserId(),
                        log.getUsername(),
                        log.getUserRole(),
                        log.getOperationModule(),
                        log.getOperationType(),
                        log.getOperationContent(),
                        log.getRequestPath(),
                        log.getCreatedAt()
                ))
                .toList();
    }
}
