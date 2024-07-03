package com.shahriar.CSE_Alumni_backend.Repos;

import com.shahriar.CSE_Alumni_backend.Entities.JobPost;
import com.shahriar.CSE_Alumni_backend.Entities.Register;
import com.shahriar.CSE_Alumni_backend.Entities.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegRepoIF extends JpaRepository<Register, Long> {

    Optional<Register> findByStudentId(String fileName);

    Optional<Register> findByEmail(String email);

    List<Register> findByUserStatus(UserStatus userStatus);

    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM register rg WHERE rg.prof_details LIKE %:query%", nativeQuery = true)
    List<Register> findByDescriptionContaining(@Param("query") String query);


}
