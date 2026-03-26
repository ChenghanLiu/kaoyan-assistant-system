package com.kaoyan.assistant.application.dashboard.dto;

import com.kaoyan.assistant.application.content.dto.MaterialResponse;
import com.kaoyan.assistant.application.content.dto.PostResponse;

import java.util.List;

public record StudentHomeSummaryResponse(
        long schoolCount,
        long majorCount,
        long materialCount,
        long postCount,
        long examPaperCount,
        List<MaterialResponse> latestMaterials,
        List<PostResponse> latestPosts
) {
}
