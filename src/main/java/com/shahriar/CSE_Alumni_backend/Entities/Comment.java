package com.shahriar.CSE_Alumni_backend.Entities;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Entity
@Data
@Builder

@AllArgsConstructor
@NoArgsConstructor

@Getter
@Setter

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


}
