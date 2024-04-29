package com.shahriar.CSE_Alumni_backend.Entities;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Entity
@Data
@Builder

public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public LocalDateTime commentedAt;

    private String commenter;


    @Column(columnDefinition = "TEXT")
    public String textContent; // Text content of the comment

    @Lob
    @Column(length=1000000000)
    private String resume;

    @Lob
    @Column(length = 1000000000)
    private byte[] decodedResume;

    @ManyToOne()
    @JoinColumn(name = "job_id", nullable = false)
    public JobPost jobPost;












    public byte[] getDecodedResume() {
        return decodedResume;
    }

    public void setDecodedResume(byte[] decodedResume) {
        this.decodedResume = decodedResume;
    }


    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

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

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }


    public Comment(Long id, LocalDateTime commentedAt, String commenter, String textContent, String resume, byte[] decodedResume, JobPost jobPost) {
        this.id = id;
        this.commentedAt = commentedAt;
        this.commenter = commenter;
        this.textContent = textContent;
        this.resume = resume;
        this.decodedResume = decodedResume;
        this.jobPost = jobPost;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }




    public Comment() {
    }

}
