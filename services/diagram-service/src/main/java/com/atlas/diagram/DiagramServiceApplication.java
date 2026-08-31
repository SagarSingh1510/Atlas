package com.atlas.diagram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class DiagramServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiagramServiceApplication.class, args);
    }
}
