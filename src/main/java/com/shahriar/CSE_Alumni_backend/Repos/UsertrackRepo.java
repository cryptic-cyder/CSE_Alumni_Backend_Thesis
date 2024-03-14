package com.shahriar.CSE_Alumni_backend.Repos;

import com.shahriar.CSE_Alumni_backend.Entities.UserTrack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsertrackRepo extends JpaRepository<UserTrack, Integer> {
    UserTrack findByEmail(String email);

    boolean existsByEmail(String email);
}
