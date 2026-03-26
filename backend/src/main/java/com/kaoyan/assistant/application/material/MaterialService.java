package com.kaoyan.assistant.application.material;

import com.kaoyan.assistant.application.content.dto.MaterialResponse;
import com.kaoyan.assistant.application.material.dto.MaterialCategoryResponse;
import com.kaoyan.assistant.application.material.dto.MaterialDownloadResponse;
import com.kaoyan.assistant.application.material.dto.MaterialReviewRequest;
import com.kaoyan.assistant.application.material.dto.MaterialUploadRequest;
import com.kaoyan.assistant.application.system.OperationLogService;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.Material;
import com.kaoyan.assistant.domain.entity.MaterialCategory;
import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.domain.enums.MaterialStatus;
import com.kaoyan.assistant.infrastructure.repository.MaterialCategoryRepository;
import com.kaoyan.assistant.infrastructure.repository.MaterialRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import com.kaoyan.assistant.infrastructure.storage.LocalStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialCategoryRepository materialCategoryRepository;
    private final SysUserRepository sysUserRepository;
    private final LocalStorageService localStorageService;
    private final OperationLogService operationLogService;

    public MaterialService(MaterialRepository materialRepository,
                           MaterialCategoryRepository materialCategoryRepository,
                           SysUserRepository sysUserRepository,
                           LocalStorageService localStorageService,
                           OperationLogService operationLogService) {
        this.materialRepository = materialRepository;
        this.materialCategoryRepository = materialCategoryRepository;
        this.sysUserRepository = sysUserRepository;
        this.localStorageService = localStorageService;
        this.operationLogService = operationLogService;
    }

    @Transactional(readOnly = true)
    public List<MaterialCategoryResponse> listCategories() {
        return materialCategoryRepository.findAllByOrderBySortOrderAscIdAsc()
                .stream()
                .map(category -> new MaterialCategoryResponse(category.getId(), category.getCategoryName(), category.getDescription()))
                .toList();
    }

    @Transactional
    public MaterialResponse upload(Long userId, MaterialUploadRequest request) {
        validateUploadRequest(request);
        MaterialCategory category = requireCategory(request.getCategoryId());
        String storedFileName = localStorageService.save(request.getFile());
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNullElse(request.getFile().getOriginalFilename(), "file"));

        Material material = new Material();
        material.setUserId(userId);
        material.setCategoryId(category.getId());
        material.setTitle(request.getTitle().trim());
        material.setDescription(normalizeText(request.getDescription()));
        material.setFileName(originalFileName);
        material.setFilePath(storedFileName);
        material.setFileSize(request.getFile().getSize());
        material.setDownloadCount(0);
        material.setReviewStatus(MaterialStatus.PENDING);
        material.setReviewComment("待审核");
        Material savedMaterial = materialRepository.save(material);
        operationLogService.record(toLoginUser(userId), "MATERIAL", "CREATE",
                "上传资料《" + savedMaterial.getTitle() + "》", "/api/student/materials");
        return toResponse(savedMaterial);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> listApprovedMaterials() {
        return toResponses(materialRepository.findByReviewStatusOrderByCreatedAtDesc(MaterialStatus.APPROVED));
    }

    @Transactional(readOnly = true)
    public MaterialResponse getApprovedMaterialDetail(Long materialId) {
        return toResponse(requireApprovedMaterial(materialId), Collections.emptyMap(), Collections.emptyMap());
    }

    @Transactional(readOnly = true)
    public MaterialResponse getStudentMaterialDetail(Long userId, Long materialId) {
        Material material = requireMaterial(materialId);
        if (material.getReviewStatus() != MaterialStatus.APPROVED && !material.getUserId().equals(userId)) {
            throw BusinessException.forbidden("forbidden");
        }
        return toResponse(material, loadCategoryMap(List.of(material)), loadUserMap(List.of(material)));
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> listMyMaterials(Long userId) {
        return toResponses(materialRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> listPendingReviewMaterials() {
        return toResponses(materialRepository.findByReviewStatusOrderByCreatedAtDesc(MaterialStatus.PENDING));
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> listAllMaterials() {
        return toResponses(materialRepository.findAllByOrderByCreatedAtDesc());
    }

    @Transactional
    public MaterialResponse review(Long reviewerId, Long materialId, MaterialReviewRequest request) {
        if (request.reviewStatus() == MaterialStatus.PENDING) {
            throw BusinessException.invalidInput("reviewStatus must be APPROVED or REJECTED");
        }

        Material material = requireMaterial(materialId);
        material.setReviewStatus(request.reviewStatus());
        material.setReviewComment(normalizeReviewComment(request));
        material.setReviewerId(reviewerId);
        material.setReviewedAt(LocalDateTime.now());
        Material savedMaterial = materialRepository.save(material);
        operationLogService.record(toLoginUser(reviewerId), "MATERIAL", "REVIEW",
                "审核资料《" + savedMaterial.getTitle() + "》结果为 " + savedMaterial.getReviewStatus().name(),
                "/api/admin/materials/" + materialId + "/review");
        return toResponse(savedMaterial, loadCategoryMap(List.of(savedMaterial)), loadUserMap(List.of(savedMaterial)));
    }

    @Transactional
    public MaterialDownloadResponse download(Long userId, Long materialId) {
        Material material = requireMaterial(materialId);
        boolean isOwner = material.getUserId().equals(userId);
        if (material.getReviewStatus() != MaterialStatus.APPROVED && !isOwner) {
            throw BusinessException.forbidden("forbidden");
        }

        material.setDownloadCount(material.getDownloadCount() + 1);
        materialRepository.save(material);
        return new MaterialDownloadResponse(localStorageService.load(material.getFilePath()), material.getFileName());
    }

    private Material requireMaterial(Long materialId) {
        return materialRepository.findById(materialId)
                .orElseThrow(() -> BusinessException.notFound("material not found"));
    }

    private Material requireApprovedMaterial(Long materialId) {
        return materialRepository.findByIdAndReviewStatus(materialId, MaterialStatus.APPROVED)
                .orElseThrow(() -> BusinessException.notFound("material not found"));
    }

    private MaterialCategory requireCategory(Long categoryId) {
        return materialCategoryRepository.findById(categoryId)
                .orElseThrow(() -> BusinessException.invalidInput("material category is invalid"));
    }

    private List<MaterialResponse> toResponses(List<Material> materials) {
        Map<Long, MaterialCategory> categoryMap = loadCategoryMap(materials);
        Map<Long, SysUser> userMap = loadUserMap(materials);
        return materials.stream()
                .map(material -> toResponse(material, categoryMap, userMap))
                .toList();
    }

    private MaterialResponse toResponse(Material material, Map<Long, MaterialCategory> categoryMap, Map<Long, SysUser> userMap) {
        MaterialCategory category = categoryMap.get(material.getCategoryId());
        SysUser uploader = userMap.get(material.getUserId());
        return new MaterialResponse(
                material.getId(),
                material.getCategoryId(),
                category == null ? null : category.getCategoryName(),
                material.getTitle(),
                material.getDescription(),
                material.getFileName(),
                material.getFilePath(),
                material.getFileSize(),
                material.getDownloadCount(),
                material.getReviewStatus().name(),
                material.getReviewComment(),
                material.getReviewerId(),
                material.getUserId(),
                uploader == null ? "unknown" : uploader.getUsername(),
                material.getReviewedAt(),
                material.getCreatedAt()
        );
    }

    private MaterialResponse toResponse(Material material) {
        return toResponse(material, loadCategoryMap(List.of(material)), loadUserMap(List.of(material)));
    }

    private Map<Long, MaterialCategory> loadCategoryMap(Collection<Material> materials) {
        if (materials.isEmpty()) {
            return Collections.emptyMap();
        }
        return materialCategoryRepository.findAllById(materials.stream().map(Material::getCategoryId).collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(MaterialCategory::getId, category -> category));
    }

    private Map<Long, SysUser> loadUserMap(Collection<Material> materials) {
        if (materials.isEmpty()) {
            return Collections.emptyMap();
        }
        HashSet<Long> userIds = new HashSet<>();
        for (Material material : materials) {
            userIds.add(material.getUserId());
            if (material.getReviewerId() != null) {
                userIds.add(material.getReviewerId());
            }
        }
        return sysUserRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(SysUser::getId, user -> user));
    }

    private void validateUploadRequest(MaterialUploadRequest request) {
        if (request.getFile() == null || request.getFile().isEmpty()) {
            throw BusinessException.invalidInput("file is required");
        }
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNullElse(request.getFile().getOriginalFilename(), ""));
        if (!StringUtils.hasText(originalFileName)) {
            throw BusinessException.invalidInput("file name is required");
        }
    }

    private String normalizeReviewComment(MaterialReviewRequest request) {
        String reviewComment = normalizeText(request.reviewComment());
        if (request.reviewStatus() == MaterialStatus.REJECTED && !StringUtils.hasText(reviewComment)) {
            throw BusinessException.invalidInput("reviewComment is required when reviewStatus is REJECTED");
        }
        return reviewComment;
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private LoginUser toLoginUser(Long userId) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("user not found"));
        return new LoginUser(user);
    }
}
