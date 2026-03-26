package com.kaoyan.assistant.application.content;

import com.kaoyan.assistant.application.content.dto.MaterialResponse;
import com.kaoyan.assistant.application.content.dto.PostResponse;
import com.kaoyan.assistant.application.material.MaterialService;
import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.infrastructure.repository.PostRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentQueryService {

    private final MaterialService materialService;
    private final PostRepository postRepository;
    private final SysUserRepository userRepository;

    public ContentQueryService(MaterialService materialService,
                               PostRepository postRepository,
                               SysUserRepository userRepository) {
        this.materialService = materialService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<MaterialResponse> listApprovedMaterials() {
        return materialService.listApprovedMaterials();
    }

    public List<MaterialResponse> listPendingMaterials() {
        return materialService.listPendingReviewMaterials();
    }

    public List<PostResponse> listPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getViewCount(),
                        post.getUserId(),
                        findUsername(post.getUserId()),
                        findDisplayName(post.getUserId()),
                        post.getCreatedAt()
                ))
                .toList();
    }

    private String findUsername(Long userId) {
        return userRepository.findById(userId)
                .map(SysUser::getUsername)
                .orElse("unknown");
    }

    private String findDisplayName(Long userId) {
        return userRepository.findById(userId)
                .map(SysUser::getDisplayName)
                .orElse("unknown");
    }
}
