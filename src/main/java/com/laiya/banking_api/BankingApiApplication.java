package com.laiya.banking_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@SpringBootApplication
public class BankingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingApiApplication.class, args);
	}

}

@RestController 
class HelloController {
	
	@GetMapping("/hello")
	public String helloFunction() {
		return "Bonjour Baobab.";
	}
	
}
