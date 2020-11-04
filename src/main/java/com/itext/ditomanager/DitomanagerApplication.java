package com.itext.ditomanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class DitomanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DitomanagerApplication.class, args);
	}

}
