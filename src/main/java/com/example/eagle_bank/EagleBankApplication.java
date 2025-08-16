package com.example.eagle_bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.example.eagle_bank.entity")
public class EagleBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(EagleBankApplication.class, args);
	}

}
