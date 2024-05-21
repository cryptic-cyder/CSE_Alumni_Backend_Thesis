package com.shahriar.CSE_Alumni_backend.Services;

import com.shahriar.CSE_Alumni_backend.Entities.Token;
import com.shahriar.CSE_Alumni_backend.Repos.TokenInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TokenValidation {

    public boolean isTokenValid(String request) {

        if (isPublicUrl(request)) {

            return true; // Allow access to public URLs without token validation
        }
        //System.out.println("Request is : " + request);
        boolean validationResult = validateToken(request);
        //System.out.println(validationResult);
        return validationResult;
    }


    private boolean isPublicUrl(String requestURI) {

        return requestURI.startsWith("/public/");
    }

    public String extractEmailFromToken(String token) {

        //System.out.println(parts.length);
        try {
            String[] parts = token.split("_");
            if (parts.length >= 2) {

                String tokenEmail = parts[1];

                return tokenEmail;

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
            } else {
                return null; // Token format is invalid
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Autowired
    private TokenInterface tokenInterface;


    private boolean validateToken(String token) {

        String tokenEmail = extractEmailFromToken(token);
        //System.out.println("Token email is : "+tokenEmail);

        String[] parts = token.split("_");

        LocalDateTime expiryTime = LocalDateTime.parse(parts[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (LocalDateTime.now().isAfter(expiryTime)) {
            //System.out.println("Time has expired...");
            return false; // Token has expired
        }

//        Token tokenFromDB = tokenInterface.findByToken(token);
//        System.out.println("Token from DB : "+tokenFromDB);
//        if (tokenFromDB == null)
//            return false;
//
//        return tokenFromDB.getEmail().equalsIgnoreCase(tokenEmail);

        return true;


//        if (tokenEmail == null)
//            return false;
//
//        Token tokenFromDB = tokenInterface.findByToken(token);
//
//        if (tokenFromDB == null)
//            return false;
//
//        return tokenFromDB.getEmail().equalsIgnoreCase(tokenEmail);
    }


}
