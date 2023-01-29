package com.dv.dancingvegetables;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class DancingVegetablesApplication {

    public static void main(String[] args) {
        SpringApplication.run(DancingVegetablesApplication.class, args);
    }

}
