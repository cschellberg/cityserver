package com.cityapp.cityserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.cityapp")
@Configuration
@EnableScheduling
public class CityserverApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(CityserverApplication.class, args);
	}

}
