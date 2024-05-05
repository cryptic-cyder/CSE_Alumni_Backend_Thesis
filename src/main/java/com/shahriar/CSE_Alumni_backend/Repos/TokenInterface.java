package com.shahriar.CSE_Alumni_backend.Repos;

import com.shahriar.CSE_Alumni_backend.Entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenInterface extends JpaRepository<Token, Long> {
    Token findByToken(String token);
}
