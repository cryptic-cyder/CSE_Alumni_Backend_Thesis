package com.shahriar.CSE_Alumni_backend.Services;

import com.shahriar.CSE_Alumni_backend.Repos.TokenInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AdminTokenValidation {
    @Autowired
    private TokenInterface tokenInterface;


    public boolean isTokenValid(String request) {

        if (isPublicUrl(request)) {
            return true;
        }

        return validateToken(request);
    }

    public boolean isPublicUrl(String requestURI) {

        return requestURI.startsWith("/public/");
    }

    private boolean validateToken(String token) {

        String[] parts = token.split("_");

        LocalDateTime tokenExpiryTime = LocalDateTime.parse(parts[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (LocalDateTime.now().isAfter(tokenExpiryTime)) {

            return false; // Token has expired
        }

        return true;
    }

    public String extractEmailFromToken(String token) {

        try {
            String[] parts = token.split("_");
            if (parts.length >= 3) {

                String tokenEmail = parts[1];

                return tokenEmail;

            }
            else {
                return null; // Token format is invalid
            }
        }
        catch (Exception e) {
            return null;
        }
    }

}
