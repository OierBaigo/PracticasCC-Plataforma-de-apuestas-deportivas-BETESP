package com.match_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.match_service.security.ServiceTokenProvider;

@SpringBootApplication
@EnableScheduling
public class MatchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchServiceApplication.class, args);
	}
	/* Para prueba manual del Settlement
	@Bean
	CommandLineRunner printServiceToken(ServiceTokenProvider provider) {
	    return args -> {
	        System.out.println("TOKEN SERVICE: " + provider.createToken());
	    };
	}
	*/
}