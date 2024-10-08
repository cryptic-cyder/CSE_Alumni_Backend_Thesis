
//        String tokenId = parts[3];
//        Long tokenIdLong = Long.parseLong(tokenId);
//
//        Optional<Token> tokenFromDB = tokenInterface.findById(tokenIdLong);
//        Token tokenDB = tokenFromDB.get();
//
//        if (tokenDB == null)
//            return false;
//
//        return tokenDB.getEmail().equalsIgnoreCase(tokenEmail);



//                LocalDateTime expiryTime = LocalDateTime.parse(parts[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//                //System.out.println(LocalDateTime.now() + " " + expiryTime);
//                if (LocalDateTime.now().isAfter(expiryTime)) {
//
//                    return null; // Token has expired
//                }
//                // Retrieve token from DB
//                Token tokenFromDB = tokenInterface.findByToken(token);
//                if (tokenFromDB == null)
//                    return null;
//                return tokenFromDB.getEmail();



package com.shahriar.CSE_Alumni_backend.Services;

import com.shahriar.CSE_Alumni_backend.Entities.Token;
import com.shahriar.CSE_Alumni_backend.Repos.TokenInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class TokenValidation {


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

        //String tokenEmail = extractEmailFromToken(token);

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
