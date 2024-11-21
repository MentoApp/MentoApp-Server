package com.mentit.mento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableAspectJAutoProxy
public class MentoAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentoAppApplication.class, args);
    }

}
