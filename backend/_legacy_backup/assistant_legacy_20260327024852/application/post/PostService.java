package com.kaoyan.assistant.application.post;

import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.Post;
import com.kaoyan.assistant.domain.entity.PostComment;
import com.kaoyan.assistant.infrastructure.repository.PostCommentRepository;
import com.kaoyan.assistant.infrastructure.repository.PostRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    public List<Post> listPosts() {
        return postRepository.findAll().stream().sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())).toList();
    }

    public Post createPost(Long userId, PostCommand command) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(command.title());
        post.setContent(command.content());
        return postRepository.save(post);
    }

    public Map<String, Object> getPostDetail(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException("Post not found"));
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        List<PostComment> comments = postCommentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        return Map.of("post", post, "comments", comments);
    }

    public PostComment addComment(Long userId, Long postId, CommentCommand command) {
        if (!postRepository.existsById(postId)) {
            throw new BusinessException("Post not found");
        }
        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(command.content());
        return postCommentRepository.save(comment);
    }

    public record PostCommand(@NotBlank String title, @NotBlank String content) {
    }

    public record CommentCommand(@NotBlank String content) {
    }
}
