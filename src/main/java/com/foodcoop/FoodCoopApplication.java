package com.foodcoop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Weekly pickup ordering system for a volunteer-run community food cooperative.
 */
@SpringBootApplication
public class FoodCoopApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodCoopApplication.class, args);
    }
}
