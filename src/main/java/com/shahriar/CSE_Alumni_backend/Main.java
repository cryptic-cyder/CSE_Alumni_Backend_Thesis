//	@Bean
//	public WebMvcConfigurer configure(){
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry reg){
//		        reg.addMapping("/**").allowedOrigins("http://localhost:3000");
//			}
//		};
//	}


package com.shahriar.CSE_Alumni_backend;

import com.shahriar.CSE_Alumni_backend.Services.RegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		SpringApplication.run(Main.class, args);
	}
}