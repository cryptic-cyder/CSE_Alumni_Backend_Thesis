package com.shahriar.CSE_Alumni_backend.Services;


import com.shahriar.CSE_Alumni_backend.Entities.Token;
import com.shahriar.CSE_Alumni_backend.Repos.TokenInterface;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptorService implements HandlerInterceptor {

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
            // If token is invalid, return unauthorized status
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false; // Stop processing the request
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

    @Autowired
    private MyJwtSecret myJwtSecret;

    private boolean validateToken(String token) {

        String secret = myJwtSecret.returnSecret();
        //System.out.println(secret);

        String tokenEmail = extractEmailFromToken(token, secret);

        Token tokenFromDB = tokenInterface.findByToken(token);

        if(tokenFromDB==null)
            return false;
        //System.out.println(tokenFromDB.getEmail()+" "+tokenEmail);
        return tokenFromDB.getEmail().equalsIgnoreCase(tokenEmail);
    }
    public static String extractEmailFromToken(String token, String secret) {

        String[] parts = token.split("_");

        if (parts.length >= 2) {
            return parts[1];
        } else {
            return null; // Token format is invalid
        }
    }

}
