package com.kaoyan.assistant.application.material;

import com.kaoyan.assistant.application.material.dto.MaterialReviewRequest;
import com.kaoyan.assistant.application.system.OperationLogService;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.Material;
import com.kaoyan.assistant.domain.enums.MaterialStatus;
import com.kaoyan.assistant.infrastructure.repository.MaterialCategoryRepository;
import com.kaoyan.assistant.infrastructure.repository.MaterialRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import com.kaoyan.assistant.infrastructure.storage.LocalStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MaterialServiceTest {

    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final MaterialCategoryRepository materialCategoryRepository = mock(MaterialCategoryRepository.class);
    private final SysUserRepository sysUserRepository = mock(SysUserRepository.class);
    private final LocalStorageService localStorageService = mock(LocalStorageService.class);
    private final OperationLogService operationLogService = mock(OperationLogService.class);
    private final MaterialService materialService = new MaterialService(
            materialRepository, materialCategoryRepository, sysUserRepository, localStorageService, operationLogService
    );

    @Test
    void shouldForbidDownloadingPendingMaterialFromAnotherUser() {
        Material material = new Material();
        material.setId(1L);
        material.setUserId(2L);
        material.setReviewStatus(MaterialStatus.PENDING);
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));

        BusinessException exception = assertThrows(BusinessException.class, () -> materialService.download(3L, 1L));

        assertEquals(4030, exception.getCode());
        assertEquals("forbidden", exception.getMessage());
    }

    @Test
    void shouldAllowOwnerDownloadingPendingMaterial() {
        Material material = new Material();
        material.setId(1L);
        material.setUserId(2L);
        material.setFileName("notes.pdf");
        material.setFilePath("notes.pdf");
        material.setDownloadCount(0);
        material.setReviewStatus(MaterialStatus.PENDING);
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(localStorageService.load("notes.pdf")).thenReturn(new ByteArrayResource("demo".getBytes()));

        materialService.download(2L, 1L);

        assertEquals(1, material.getDownloadCount());
        verify(materialRepository).save(material);
    }

    @Test
    void shouldRequireReviewCommentWhenRejectingMaterial() {
        Material material = new Material();
        material.setId(1L);
        material.setUserId(2L);
        material.setReviewStatus(MaterialStatus.PENDING);
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> materialService.review(1L, 1L, new MaterialReviewRequest(MaterialStatus.REJECTED, " ")));

        assertEquals(4000, exception.getCode());
        assertEquals("reviewComment is required when reviewStatus is REJECTED", exception.getMessage());
    }
}
