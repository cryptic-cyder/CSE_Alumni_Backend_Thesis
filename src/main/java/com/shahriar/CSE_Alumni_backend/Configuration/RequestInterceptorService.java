package com.shahriar.CSE_Alumni_backend.Configuration;

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
            System.out.println("This is public url....");
            return true; // Allow access to public URLs without token validation
        }

        //System.out.println("This is protected API...");

        String token = extractTokenFromRequest(request);

        System.out.println("Request is: " + request.toString());
        System.out.println("Token is: " + token);

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

    private String extractTokenFromRequest(HttpServletRequest request) {

        //System.out.println(request);
        // Extract token from the Authorization header
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


























//package com.shahriar.CSE_Alumni_backend.Configuration;
//
//import com.shahriar.CSE_Alumni_backend.Services.JwtTokenFilter;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import javax.servlet.Filter;
//
//@Configuration
//@EnableWebSecurity
//public class CrosConfig extends WebSecurityConfigurerAdapter {
//
//    @Value("${app.allowedOrigins}")
//    private String[] allowedOrigins;
//
//    @Autowired
//    private JwtTokenFilter jwtTokenFilter;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/public/**").permitAll() // Allow access to public endpoints without authentication
//                .anyRequest().authenticated()
//                .and()
//                .addFilterBefore(new JwtTokenFilter(), Filter.class);
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Configuration
//    public class CorsConfig implements WebMvcConfigurer {
//        @Override
//        public void addCorsMappings(CorsRegistry registry) {
//            registry.addMapping("/**")
//                    .allowedOrigins(allowedOrigins)
//                    .allowedMethods("GET", "POST", "PUT", "DELETE")
//                    .allowedHeaders("*")
//                    .allowCredentials(true);
//        }
//    }
//}