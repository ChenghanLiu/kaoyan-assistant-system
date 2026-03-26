package com.kaoyan.assistant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "wrong_question")
public class WrongQuestion extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "paper_id", nullable = false)
    private Long paperId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "latest_record_id")
    private Long latestRecordId;

    @Column(name = "latest_answer", length = 255)
    private String latestAnswer;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getLatestRecordId() {
        return latestRecordId;
    }

    public void setLatestRecordId(Long latestRecordId) {
        this.latestRecordId = latestRecordId;
    }

    public String getLatestAnswer() {
        return latestAnswer;
    }

    public void setLatestAnswer(String latestAnswer) {
        this.latestAnswer = latestAnswer;
    }
}
