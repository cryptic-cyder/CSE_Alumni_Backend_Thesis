package com.shahriar.CSE_Alumni_backend.Repos;

import com.shahriar.CSE_Alumni_backend.Entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.*;

public interface TokenInterface extends JpaRepository<Token, Long> {
    Token findByToken(String token);

    @Query("SELECT t FROM Token t WHERE t.timeOut < :currentTime")
    List<Token> findExpiredTokens(LocalDateTime currentTime);
}
