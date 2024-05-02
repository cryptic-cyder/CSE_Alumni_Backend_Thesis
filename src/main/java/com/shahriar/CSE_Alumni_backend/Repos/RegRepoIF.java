package com.shahriar.CSE_Alumni_backend.Repos;

import com.shahriar.CSE_Alumni_backend.Entities.Register;
import com.shahriar.CSE_Alumni_backend.Entities.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegRepoIF extends JpaRepository<Register, Long> {

    Optional<Register> findByStudentId(String fileName);

    Optional<Register> findByEmail(String email);

    List<Register> findByUserStatus(UserStatus userStatus);

    boolean existsByEmail(String email);


}
