package com.shahriar.CSE_Alumni_backend.Repos;

import com.shahriar.CSE_Alumni_backend.Entities.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPostInterface extends JpaRepository<JobPost, Long> {

    List<JobPost> findByUserEmail(String userEmail);
}
