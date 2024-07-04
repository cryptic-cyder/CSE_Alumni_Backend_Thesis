package com.shahriar.CSE_Alumni_backend.Repos;

import com.shahriar.CSE_Alumni_backend.Entities.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobPostInterface extends JpaRepository<JobPost, Long> {

    List<JobPost> findByUserEmail(String userEmail);

    @Query(value = "SELECT * FROM job_post jp WHERE jp.requirements or jp.responsibilities or jp.title LIKE %:query%", nativeQuery = true)
    List<JobPost> findByDescriptionContaining(@Param("query") String query);

}
