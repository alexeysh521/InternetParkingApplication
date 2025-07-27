package com.example.RETURN;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
public class ReturnApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReturnApplication.class, args);
	}


}
