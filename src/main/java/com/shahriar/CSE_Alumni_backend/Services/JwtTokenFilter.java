//package com.shahriar.CSE_Alumni_backend.Services;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import java.io.IOException;
//
//@Component
//public class JwtTokenFilter implements Filter {
//
//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, javax.servlet.FilterChain filterChain) throws IOException, javax.servlet.ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        String token = jwtTokenProvider.resolveToken(request);
//        if (token != null && jwtTokenProvider.validateToken(token)) {
//            Authentication auth = jwtTokenProvider.getAuthentication(token);
//            if (auth != null) {
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        }
//
//        filterChain.doFilter(servletRequest, servletResponse);
//
//    }
//
//    @Override
//    public void init(FilterConfig filterConfig) {
//        // Initialization code, if needed
//    }
//
//
//
//    @Override
//    public void destroy() {
//        // Cleanup code, if needed
//    }
//}