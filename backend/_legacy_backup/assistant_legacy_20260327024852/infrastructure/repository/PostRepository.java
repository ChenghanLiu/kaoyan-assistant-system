package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
