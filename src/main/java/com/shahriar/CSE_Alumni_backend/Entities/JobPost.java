package com.shahriar.CSE_Alumni_backend.Entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String company;
    private String vacancy;
    private String location;


    @Lob
    @Column(length = 10000000)
    private String requirements;

    @Lob
    @Column(length = 10000000)
    private String responsibilities;

    @Lob
    @Column(length = 10000000)
    private String salary;

    private LocalDateTime postedAt;

    @Lob
    @Column(length = 1000000000)
    private List<String> images = new ArrayList<>();

    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    private String title;

    private String userEmail;



    private List<byte[]> decodedImages;


}
