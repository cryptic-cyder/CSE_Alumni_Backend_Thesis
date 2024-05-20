package com.shahriar.CSE_Alumni_backend.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resumes/**")
                .addResourceLocations("file:///C:/Users/HP/Desktop/Intellij Spring boot/CSE_Alumni_Backend_Thesis/Resumes/");
    }


}

