package de.raychouni;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class OrderManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderManagerApplication.class, args);
    }
}
