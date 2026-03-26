package com.kaoyan.assistant.application.content;

import com.kaoyan.assistant.application.content.dto.PostCommentCreateRequest;
import com.kaoyan.assistant.application.content.dto.PostCommentResponse;
import com.kaoyan.assistant.application.content.dto.PostCreateRequest;
import com.kaoyan.assistant.application.content.dto.PostDetailResponse;
import com.kaoyan.assistant.application.content.dto.PostResponse;
import com.kaoyan.assistant.application.system.OperationLogService;
import com.kaoyan.assistant.common.security.LoginUser;
import com.kaoyan.assistant.common.exception.BusinessException;
import com.kaoyan.assistant.domain.entity.Post;
import com.kaoyan.assistant.domain.entity.PostComment;
import com.kaoyan.assistant.domain.entity.SysUser;
import com.kaoyan.assistant.infrastructure.repository.PostCommentRepository;
import com.kaoyan.assistant.infrastructure.repository.PostRepository;
import com.kaoyan.assistant.infrastructure.repository.SysUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final SysUserRepository userRepository;
    private final OperationLogService operationLogService;

    public PostService(PostRepository postRepository,
                       PostCommentRepository postCommentRepository,
                       SysUserRepository userRepository,
                       OperationLogService operationLogService) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.userRepository = userRepository;
        this.operationLogService = operationLogService;
    }

    @Transactional(readOnly = true)
    public List<PostResponse> listPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toPostResponse)
                .toList();
    }

    @Transactional
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = findPost(postId);
        post.setViewCount(post.getViewCount() + 1);
        Post savedPost = postRepository.save(post);
        return toPostDetailResponse(savedPost);
    }

    @Transactional
    public PostResponse createPost(Long userId, PostCreateRequest request) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.title().trim());
        post.setContent(request.content().trim());
        post.setViewCount(0);
        Post savedPost = postRepository.save(post);
        operationLogService.record(toLoginUser(userId), "POST", "CREATE",
                "发布帖子《" + savedPost.getTitle() + "》", "/api/posts");
        return toPostResponse(savedPost);
    }

    @Transactional
    public void deletePost(Long postId, Long userId, boolean isAdmin) {
        Post post = findPost(postId);
        validateOwnership(post.getUserId(), userId, isAdmin, "you can only delete your own post");
        postCommentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .forEach(postCommentRepository::delete);
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<PostCommentResponse> listComments(Long postId) {
        ensurePostExists(postId);
        return postCommentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    @Transactional
    public PostCommentResponse createComment(Long postId, Long userId, PostCommentCreateRequest request) {
        ensurePostExists(postId);
        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(request.content().trim());
        PostComment savedComment = postCommentRepository.save(comment);
        operationLogService.record(toLoginUser(userId), "POST", "COMMENT",
                "发布帖子评论，帖子ID=" + postId, "/api/posts/" + postId + "/comments");
        return toCommentResponse(savedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId, boolean isAdmin) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> BusinessException.notFound("comment not found"));
        validateOwnership(comment.getUserId(), userId, isAdmin, "you can only delete your own comment");
        postCommentRepository.delete(comment);
    }

    private void ensurePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw BusinessException.notFound("post not found");
        }
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("post not found"));
    }

    private void validateOwnership(Long resourceUserId, Long currentUserId, boolean isAdmin, String message) {
        if (!isAdmin && !resourceUserId.equals(currentUserId)) {
            throw BusinessException.forbidden(message);
        }
    }

    private PostResponse toPostResponse(Post post) {
        SysUser user = findUser(post.getUserId());
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getUserId(),
                user.getUsername(),
                user.getDisplayName(),
                post.getCreatedAt()
        );
    }

    private PostDetailResponse toPostDetailResponse(Post post) {
        SysUser user = findUser(post.getUserId());
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getUserId(),
                user.getUsername(),
                user.getDisplayName(),
                post.getCreatedAt()
        );
    }

    private PostCommentResponse toCommentResponse(PostComment comment) {
        SysUser user = findUser(comment.getUserId());
        return new PostCommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getUserId(),
                user.getUsername(),
                user.getDisplayName(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    private SysUser findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("user not found"));
    }

    private LoginUser toLoginUser(Long userId) {
        return new LoginUser(findUser(userId));
    }
}
