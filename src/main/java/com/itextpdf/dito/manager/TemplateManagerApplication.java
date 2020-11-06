package com.itextpdf.dito.manager;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@Log4j2
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class TemplateManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemplateManagerApplication.class, args);
    }

}
