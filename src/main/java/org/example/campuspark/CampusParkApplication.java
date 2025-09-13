package org.example.campuspark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@ConfigurationPropertiesScan
public class CampusParkApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusParkApplication.class, args);
    }

}
