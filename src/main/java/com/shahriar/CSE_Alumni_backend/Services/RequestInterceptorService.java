package com.shahriar.CSE_Alumni_backend.Services;

import com.shahriar.CSE_Alumni_backend.Entities.Token;
import com.shahriar.CSE_Alumni_backend.Repos.TokenInterface;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RequestInterceptorService implements HandlerInterceptor {

    String tokenEmailUsedToOtherClass="";

    public String getTokenEmailUsedToOtherClass() {
        return tokenEmailUsedToOtherClass;
    }

    public void setTokenEmailUsedToOtherClass(String tokenEmailUsedToOtherClass){
        this.tokenEmailUsedToOtherClass = tokenEmailUsedToOtherClass;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String requestURI = request.getRequestURI();

        if (isPublicUrl(requestURI)) {
            return true; // Allow access to public URLs without token validation
        }

        String token = extractToken(request);

        boolean isValidToken = validateToken(token);
        //System.out.println(isValidToken);
        if (!isValidToken) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try {
                // Set response body with the custom message
                response.getWriter().write("Invalid token. You are not logged in or time is out...Plz login again");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }

        // Token is valid, continue with the request
        return true;
    }

    private boolean isPublicUrl(String requestURI) {

        return requestURI.startsWith("/public/");
    }

    private String extractToken(HttpServletRequest request) {
        // Extract token from request headers
        String authorizationHeader = request.getHeader("Authorization");

        // Check if Authorization header is present and starts with "Bearer"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token part after "Bearer "
            return authorizationHeader.substring(7); // 7 is the length of "Bearer "
        }

        return null; // Return null if token is not found or is in an unexpected format
    }


    @Autowired
    private TokenInterface tokenInterface;

    //@Autowired
    //private MyJwtSecret myJwtSecret;

    private boolean validateToken(String token) {

        String tokenEmail = extractEmailFromToken(token);

        setTokenEmailUsedToOtherClass(tokenEmail);

        if (tokenEmail == null)
            return false;

        Token tokenFromDB = tokenInterface.findByToken(token);

        if (tokenFromDB == null)
            return false;

        return tokenFromDB.getEmail().equalsIgnoreCase(tokenEmail);
    }

    public String extractEmailFromToken(String token) {

        //System.out.println(parts.length);
        try {
            String[] parts = token.split("_");
            if (parts.length >= 2) {

                String tokenEmail = parts[1];
                LocalDateTime expiryTime = LocalDateTime.parse(parts[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                System.out.println(LocalDateTime.now() + " " + expiryTime);
                if (LocalDateTime.now().isAfter(expiryTime)) {

                    return null; // Token has expired
                }
                // Retrieve token from DB
                Token tokenFromDB = tokenInterface.findByToken(token);
                if (tokenFromDB == null)
                    return null;
                return tokenFromDB.getEmail();
            } else {
                return null; // Token format is invalid
            }
        } catch (Exception e) {
           return null;
        }
    }
}


