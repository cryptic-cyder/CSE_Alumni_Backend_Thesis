package com.shahriar.CSE_Alumni_backend.Entities;

import jakarta.persistence.Entity;
import lombok.*;
import java.util.List;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class JobPostDTO {

    private Long id;
    private String description;
    private LocalDateTime postedAt;
    private List<String> decodedImages;
    private String title;
    private String userEmail;
    private List<CommentDTO> comments;
}
