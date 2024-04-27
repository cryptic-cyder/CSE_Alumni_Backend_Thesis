package com.shahriar.CSE_Alumni_backend.Entities;

import jakarta.persistence.Entity;
import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public class JobPostDTO {

    private String title;
    private String userEmail;
    private LocalDateTime postedAt;

    // Constructor
    public JobPostDTO( String title, String userEmail, LocalDateTime postedAt) {

        this.title = title;
        this.userEmail = userEmail;
        this.postedAt = postedAt;
    }

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }
}
