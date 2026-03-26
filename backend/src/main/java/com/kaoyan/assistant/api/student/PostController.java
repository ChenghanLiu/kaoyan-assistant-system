package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.content.PostService;
import com.kaoyan.assistant.application.content.dto.PostCommentCreateRequest;
import com.kaoyan.assistant.application.content.dto.PostCommentResponse;
import com.kaoyan.assistant.application.content.dto.PostCreateRequest;
import com.kaoyan.assistant.application.content.dto.PostDetailResponse;
import com.kaoyan.assistant.application.content.dto.PostResponse;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.common.util.SecurityUtils;
import com.kaoyan.assistant.common.model.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ApiResponse<List<PostResponse>> listPosts() {
        return ApiResponse.success(postService.listPosts());
    }

    @GetMapping("/{id}")
    public ApiResponse<PostDetailResponse> getPost(@PathVariable Long id) {
        return ApiResponse.success(postService.getPostDetail(id));
    }

    @PostMapping
    public ApiResponse<PostResponse> createPost(@Valid @RequestBody PostCreateRequest request) {
        return ApiResponse.success("post created", postService.createPost(getLoginUser().getId(), request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id, getLoginUser().getId(), false);
        return ApiResponse.success("post deleted", null);
    }

    @PostMapping("/{id}/comments")
    public ApiResponse<PostCommentResponse> createComment(@PathVariable Long id,
                                                          @Valid @RequestBody PostCommentCreateRequest request) {
        return ApiResponse.success("comment created", postService.createComment(id, getLoginUser().getId(), request));
    }

    @GetMapping("/{id}/comments")
    public ApiResponse<List<PostCommentResponse>> listComments(@PathVariable Long id) {
        return ApiResponse.success(postService.listComments(id));
    }

    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id) {
        postService.deleteComment(id, getLoginUser().getId(), false);
        return ApiResponse.success("comment deleted", null);
    }

    private LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }
}
