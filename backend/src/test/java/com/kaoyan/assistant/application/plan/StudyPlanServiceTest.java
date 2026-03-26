package com.kaoyan.assistant.application.plan;

import com.kaoyan.assistant.application.system.OperationLogService;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.StudyPlan;
import com.kaoyan.assistant.domain.entity.StudyTask;
import com.kaoyan.assistant.infrastructure.repository.StudyPlanRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyProgressRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyReminderRepository;
import com.kaoyan.assistant.infrastructure.repository.StudyTaskRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StudyPlanServiceTest {

    private final StudyPlanRepository studyPlanRepository = mock(StudyPlanRepository.class);
    private final StudyTaskRepository studyTaskRepository = mock(StudyTaskRepository.class);
    private final StudyProgressRepository studyProgressRepository = mock(StudyProgressRepository.class);
    private final StudyReminderRepository studyReminderRepository = mock(StudyReminderRepository.class);
    private final SysUserRepository sysUserRepository = mock(SysUserRepository.class);
    private final OperationLogService operationLogService = mock(OperationLogService.class);
    private final StudyPlanService studyPlanService = new StudyPlanService(
            studyPlanRepository, studyTaskRepository, studyProgressRepository, studyReminderRepository,
            sysUserRepository, operationLogService
    );

    @Test
    void shouldForbidAccessingAnotherUsersPlan() {
        StudyPlan plan = new StudyPlan();
        plan.setId(10L);
        plan.setUserId(2L);
        when(studyPlanRepository.findById(10L)).thenReturn(Optional.of(plan));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> studyPlanService.getPlanDetail(3L, 10L));

        assertEquals(4030, exception.getCode());
        assertEquals("forbidden", exception.getMessage());
    }

    @Test
    void shouldForbidUpdatingAnotherUsersTask() {
        StudyTask task = new StudyTask();
        task.setId(20L);
        task.setUserId(2L);
        when(studyTaskRepository.findById(20L)).thenReturn(Optional.of(task));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> studyPlanService.updateTaskStatus(3L, 20L, com.kaoyan.assistant.domain.enums.TaskStatus.DONE));

        assertEquals(4030, exception.getCode());
        assertEquals("forbidden", exception.getMessage());
    }
}
