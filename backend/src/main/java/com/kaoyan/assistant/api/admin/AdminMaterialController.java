package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.content.dto.MaterialResponse;
import com.kaoyan.assistant.application.material.MaterialService;
import com.kaoyan.assistant.application.material.dto.MaterialReviewRequest;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/materials")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class AdminMaterialController {

    private final MaterialService materialService;

    public AdminMaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public ApiResponse<List<MaterialResponse>> listAll() {
        return ApiResponse.success(materialService.listAllMaterials());
    }

    @GetMapping("/reviews/pending")
    public ApiResponse<List<MaterialResponse>> pending() {
        return ApiResponse.success(materialService.listPendingReviewMaterials());
    }

    @PatchMapping("/{id}/review")
    public ApiResponse<MaterialResponse> review(@PathVariable @Positive(message = "id must be greater than 0") Long id,
                                                @Valid @RequestBody MaterialReviewRequest request) {
        return ApiResponse.success("review success",
                materialService.review(SecurityUtils.getLoginUser().getId(), id, request));
    }
}
