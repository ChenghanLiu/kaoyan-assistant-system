package com.kaoyan.assistant.application.user;

import com.kaoyan.assistant.domain.entity.OperationLog;
import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.infrastructure.repository.OperationLogRepository;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    public OperationLogService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    public void save(SysUser user, String type, String content) {
        OperationLog operationLog = new OperationLog();
        if (user != null) {
            operationLog.setUserId(user.getId());
            operationLog.setUsername(user.getUsername());
        }
        operationLog.setOperationType(type);
        operationLog.setOperationContent(content);
        operationLogRepository.save(operationLog);
    }
}
