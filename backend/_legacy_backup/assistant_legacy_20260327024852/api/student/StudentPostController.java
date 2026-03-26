package com.kaoyan.assistant.api.student;

import com.kaoyan.assistant.application.post.PostService;
import com.kaoyan.assistant.common.model.ApiResponse;
import com.kaoyan.assistant.common.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/posts")
@RequiredArgsConstructor
public class StudentPostController {

    private final PostService postService;

    @GetMapping
    public ApiResponse<?> listPosts() {
        return ApiResponse.success(postService.listPosts());
    }

    @PostMapping
    public ApiResponse<?> createPost(@Valid @RequestBody PostService.PostCommand command) {
        return ApiResponse.success("Publish success", postService.createPost(SecurityUtils.getLoginUser().getId(), command));
    }

    @GetMapping("/{postId}")
    public ApiResponse<?> getPost(@PathVariable Long postId) {
        return ApiResponse.success(postService.getPostDetail(postId));
    }

    @PostMapping("/{postId}/comments")
    public ApiResponse<?> addComment(@PathVariable Long postId, @Valid @RequestBody PostService.CommentCommand command) {
        return ApiResponse.success("Comment success", postService.addComment(SecurityUtils.getLoginUser().getId(), postId, command));
    }
}
