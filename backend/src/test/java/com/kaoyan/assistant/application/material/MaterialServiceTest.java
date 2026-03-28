package com.kaoyan.assistant.application.material;

import com.kaoyan.assistant.application.material.dto.MaterialUploadRequest;
import com.kaoyan.assistant.application.material.dto.MaterialReviewRequest;
import com.kaoyan.assistant.application.system.OperationLogService;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.Material;
import com.kaoyan.assistant.domain.entity.MaterialCategory;
import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.domain.enums.MaterialStatus;
import com.kaoyan.assistant.infrastructure.repository.MaterialCategoryRepository;
import com.kaoyan.assistant.infrastructure.repository.MaterialRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import com.kaoyan.assistant.infrastructure.storage.LocalStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;

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
    void shouldUploadMaterialAndPersistStorageMetadata() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "notes.pdf", "application/pdf", "pdf-content".getBytes()
        );
        MaterialUploadRequest request = new MaterialUploadRequest();
        request.setCategoryId(9L);
        request.setTitle(" 408 高频题 ");
        request.setDescription(" 强化阶段整理 ");
        request.setFile(file);

        MaterialCategory category = new MaterialCategory();
        category.setId(9L);
        category.setCategoryName("专业课");

        SysUser uploader = new SysUser();
        uploader.setId(2L);
        uploader.setUsername("student");
        uploader.setDisplayName("Student Demo");

        when(materialCategoryRepository.findById(9L)).thenReturn(Optional.of(category));
        when(localStorageService.save(file)).thenReturn("stored-notes.pdf");
        when(sysUserRepository.findById(2L)).thenReturn(Optional.of(uploader));
        when(sysUserRepository.findAllById(any())).thenReturn(List.of(uploader));
        when(materialCategoryRepository.findAllById(any())).thenReturn(List.of(category));
        when(materialRepository.save(any(Material.class))).thenAnswer(invocation -> {
            Material material = invocation.getArgument(0);
            material.setId(18L);
            material.setCreatedAt(LocalDateTime.now());
            material.setUpdatedAt(LocalDateTime.now());
            return material;
        });
        doNothing().when(operationLogService).record(any(), any(), any(), any(), any());

        var response = materialService.upload(2L, request);

        ArgumentCaptor<Material> materialCaptor = ArgumentCaptor.forClass(Material.class);
        verify(materialRepository, times(1)).save(materialCaptor.capture());
        Material savedMaterial = materialCaptor.getValue();
        assertEquals(2L, savedMaterial.getUserId());
        assertEquals(9L, savedMaterial.getCategoryId());
        assertEquals("408 高频题", savedMaterial.getTitle());
        assertEquals("强化阶段整理", savedMaterial.getDescription());
        assertEquals("notes.pdf", savedMaterial.getFileName());
        assertEquals("stored-notes.pdf", savedMaterial.getFilePath());
        assertEquals(file.getSize(), savedMaterial.getFileSize());
        assertEquals(MaterialStatus.PENDING, savedMaterial.getReviewStatus());
        assertEquals("待审核", savedMaterial.getReviewComment());
        assertEquals("stored-notes.pdf", response.filePath());
        assertEquals("notes.pdf", response.fileName());
    }

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
