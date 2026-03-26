package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.content.ContentQueryService;
import com.kaoyan.assistant.application.content.dto.MaterialResponse;
import com.kaoyan.assistant.application.material.MaterialService;
import com.kaoyan.assistant.common.model.ApiResponse;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
@Validated
public class MaterialController {

    private final ContentQueryService contentQueryService;
    private final MaterialService materialService;

    public MaterialController(ContentQueryService contentQueryService, MaterialService materialService) {
        this.contentQueryService = contentQueryService;
        this.materialService = materialService;
    }

    @GetMapping
    public ApiResponse<List<MaterialResponse>> listMaterials() {
        return ApiResponse.success(contentQueryService.listApprovedMaterials());
    }

    @GetMapping("/{id}")
    public ApiResponse<MaterialResponse> detail(@PathVariable @Positive(message = "id must be greater than 0") Long id) {
        return ApiResponse.success(materialService.getApprovedMaterialDetail(id));
    }
}
