package com.shahriar.CSE_Alumni_backend.Services;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptorService implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Logging request method and URL
        System.out.println("Request " + request.getMethod() + " " + request.getRequestURI());

        // Logging headers
        request.getHeaderNames().asIterator().forEachRemaining(headerName ->
                System.out.println(headerName + " : " + request.getHeader(headerName)));

        return true;
    }
}
