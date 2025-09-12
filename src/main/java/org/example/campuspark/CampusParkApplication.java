package org.example.campuspark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CampusParkApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusParkApplication.class, args);
    }

}
