package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.content.dto.MaterialResponse;
import com.kaoyan.assistant.application.material.MaterialService;
import com.kaoyan.assistant.application.material.dto.MaterialCategoryResponse;
import com.kaoyan.assistant.application.material.dto.MaterialDownloadResponse;
import com.kaoyan.assistant.application.material.dto.MaterialUploadRequest;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/student/materials")
@PreAuthorize("hasRole('STUDENT')")
@Validated
public class StudentMaterialController {

    private final MaterialService materialService;

    public StudentMaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MaterialResponse> upload(@Valid @ModelAttribute MaterialUploadRequest request) {
        return ApiResponse.success("upload success", materialService.upload(currentUserId(), request));
    }

    @GetMapping("/mine")
    public ApiResponse<List<MaterialResponse>> myMaterials() {
        return ApiResponse.success(materialService.listMyMaterials(currentUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<MaterialResponse> detail(@PathVariable @Positive(message = "id must be greater than 0") Long id) {
        return ApiResponse.success(materialService.getStudentMaterialDetail(currentUserId(), id));
    }

    @GetMapping("/categories")
    public ApiResponse<List<MaterialCategoryResponse>> categories() {
        return ApiResponse.success(materialService.listCategories());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable @Positive(message = "id must be greater than 0") Long id) {
        MaterialDownloadResponse downloadResponse = materialService.download(currentUserId(), id);
        Resource resource = downloadResponse.resource();
        String encodedName = URLEncoder.encode(downloadResponse.fileName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private Long currentUserId() {
        return SecurityUtils.getLoginUser().getId();
    }
}
