package com.shahriar.CSE_Alumni_backend.Entities;

import java.time.LocalDateTime;

public class CommentDTO {

    private Long id;
    private LocalDateTime commentedAt;
    private Long jobPostId;
    private String textContent;

    // Constructor
    public CommentDTO(Long id, LocalDateTime commentedAt, Long jobPostId, String textContent) {
        this.id = id;
        this.commentedAt = commentedAt;
        this.jobPostId = jobPostId;
        this.textContent = textContent;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCommentedAt() {
        return commentedAt;
    }

    public void setCommentedAt(LocalDateTime commentedAt) {
        this.commentedAt = commentedAt;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}
