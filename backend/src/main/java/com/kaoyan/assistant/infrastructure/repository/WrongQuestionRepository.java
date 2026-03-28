package com.kaoyan.assistant.infrastructure.repository;

import com.kaoyan.assistant.domain.entity.WrongQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WrongQuestionRepository extends JpaRepository<WrongQuestion, Long> {

    List<WrongQuestion> findByUserIdOrderByUpdatedAtDesc(Long userId);

    Optional<WrongQuestion> findByUserIdAndQuestionId(Long userId, Long questionId);

    boolean existsByUserId(Long userId);

    boolean existsByPaperId(Long paperId);

    boolean existsByQuestionId(Long questionId);
}
