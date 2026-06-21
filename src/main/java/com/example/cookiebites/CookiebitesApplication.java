package com.example.cookiebites;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;




@SpringBootApplication
public class CookiebitesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CookiebitesApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(){
		return args -> {
			//  Producto prod = new Producto();
		};
	}

}
