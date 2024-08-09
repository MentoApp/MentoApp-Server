package com.mentit.mento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity(debug = true)
public class MentoAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentoAppApplication.class, args);
    }

}
