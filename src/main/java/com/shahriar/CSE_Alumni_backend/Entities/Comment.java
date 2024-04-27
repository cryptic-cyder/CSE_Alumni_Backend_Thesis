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

    private String encodedResume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    public JobPost jobPost;

    @Lob
    @Column(length = 10000000)
    public byte[] decodedResume;







    public Comment() {

    }

    public Comment(Long id, LocalDateTime commentedAt, String commenter, String textContent, String encodedResume, JobPost jobPost, byte[] decodedResume) {
        this.id = id;
        this.commentedAt = commentedAt;
        this.commenter = commenter;
        this.textContent = textContent;
        this.encodedResume = encodedResume;
        this.jobPost = jobPost;
        this.decodedResume = decodedResume;
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

    public String getEncodedResume() {
        return encodedResume;
    }

    public void setEncodedResume(String encodedResume) {
        this.encodedResume = encodedResume;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public byte[] getDecodedResume() {
        return decodedResume;
    }

    public void setDecodedResume(byte[] decodedResume) {
        this.decodedResume = decodedResume;
    }


    /*public static Comment createComment(String textContent, MultipartFile resume, JobPost jobPost) throws IOException {

        byte[] resumeBytes = null;

        if (resume != null) {
            resumeBytes = resume.getBytes();
        }
        LocalDateTime commentedAt = LocalDateTime.now();

        return new Comment(null, textContent, resumeBytes, commentedAt, jobPost);
    }*/

}
