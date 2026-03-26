package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.material.MaterialService;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/student/materials")
@RequiredArgsConstructor
public class StudentMaterialController {

    private final MaterialService materialService;

    @GetMapping
    public ApiResponse<?> listApproved() {
        return ApiResponse.success(materialService.listApprovedMaterials());
    }

    @GetMapping("/mine")
    public ApiResponse<?> listMine() {
        return ApiResponse.success(materialService.listMyMaterials(SecurityUtils.getLoginUser().getId()));
    }

    @GetMapping("/{materialId}")
    public ApiResponse<?> detail(@PathVariable Long materialId) {
        return ApiResponse.success(materialService.getMaterial(materialId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> upload(@ModelAttribute MaterialUploadRequest request) {
        return ApiResponse.success("Upload success",
                materialService.upload(SecurityUtils.getLoginUser().getId(),
                        new MaterialService.UploadCommand(request.getCategoryId(), request.getTitle(), request.getDescription()),
                        request.getFile()));
    }

    @GetMapping("/{materialId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long materialId) {
        Resource resource = materialService.loadResource(materialId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Getter
    @Setter
    public static class MaterialUploadRequest {
        private Long categoryId;
        private String title;
        private String description;
        private MultipartFile file;
    }
}
