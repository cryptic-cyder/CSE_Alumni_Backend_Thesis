package com.shahriar.CSE_Alumni_backend.Entities;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CommentDTO {

    private Long id;
    private LocalDateTime commentedAt;
    private String commenter;
    private String textContent;
    private String decodedResume;

    // Constructor

}
