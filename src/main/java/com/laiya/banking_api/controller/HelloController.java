package com.laiya.banking_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class HelloController {
	@GetMapping("/hello")
	public String helloFunction() {
		return "Bonjour Baobab.";
	}	
}

