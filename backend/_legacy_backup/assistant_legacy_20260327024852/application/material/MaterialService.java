package com.kaoyan.assistant.application.material;

import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.Material;
import com.kaoyan.assistant.domain.enums.MaterialStatus;
import com.kaoyan.assistant.infrastructure.repository.MaterialRepository;
import com.kaoyan.assistant.infrastructure.storage.LocalStorageService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final LocalStorageService localStorageService;

    public List<Material> listApprovedMaterials() {
        return materialRepository.findByReviewStatusInOrderByCreatedAtDesc(List.of(MaterialStatus.APPROVED));
    }

    public List<Material> listMyMaterials(Long userId) {
        return materialRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Material getMaterial(Long materialId) {
        return materialRepository.findById(materialId).orElseThrow(() -> new BusinessException("Material not found"));
    }

    public Material upload(Long userId, UploadCommand command, MultipartFile file) {
        String fileName = localStorageService.save(file);
        Material material = new Material();
        material.setUserId(userId);
        material.setCategoryId(command.categoryId());
        material.setTitle(command.title());
        material.setDescription(command.description());
        material.setFileName(file.getOriginalFilename());
        material.setFilePath(fileName);
        material.setFileSize(file.getSize());
        material.setReviewStatus(MaterialStatus.PENDING);
        return materialRepository.save(material);
    }

    public Material review(Long materialId, MaterialStatus reviewStatus) {
        Material material = getMaterial(materialId);
        material.setReviewStatus(reviewStatus);
        return materialRepository.save(material);
    }

    public Resource loadResource(Long materialId) {
        Material material = getMaterial(materialId);
        return localStorageService.load(material.getFilePath());
    }

    public record UploadCommand(Long categoryId, @NotBlank String title, String description) {
    }
}
