package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    List<PostComment> findByPostIdOrderByCreatedAtAsc(Long postId);

    boolean existsByUserId(Long userId);
}
