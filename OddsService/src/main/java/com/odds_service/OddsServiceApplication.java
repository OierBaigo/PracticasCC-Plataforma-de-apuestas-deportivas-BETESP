package com.odds_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OddsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OddsServiceApplication.class, args);
	}

}
