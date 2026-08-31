package com.atlas.aireview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class AiReviewServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiReviewServiceApplication.class, args);
    }
}
