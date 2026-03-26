package com.kaoyan.assistant.api.admin;

import com.kaoyan.assistant.application.content.PostService;
import com.kaoyan.assistant.application.content.dto.PostCommentResponse;
import com.kaoyan.assistant.application.content.dto.PostResponse;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/posts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {

    private final PostService postService;

    public AdminPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ApiResponse<List<PostResponse>> listPosts() {
        return ApiResponse.success(postService.listPosts());
    }

    @GetMapping("/{id}/comments")
    public ApiResponse<List<PostCommentResponse>> listComments(@PathVariable Long id) {
        return ApiResponse.success(postService.listComments(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id, getLoginUser().getId(), true);
        return ApiResponse.success("post deleted", null);
    }

    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id) {
        postService.deleteComment(id, getLoginUser().getId(), true);
        return ApiResponse.success("comment deleted", null);
    }

    private LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }
}
