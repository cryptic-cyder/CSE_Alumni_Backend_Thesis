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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}