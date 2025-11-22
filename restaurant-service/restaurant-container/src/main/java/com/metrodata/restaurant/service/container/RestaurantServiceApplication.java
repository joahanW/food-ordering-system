package com.metrodata.restaurant.service.container;

import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.metrodata.restaurant.service.dataaccess",
                                    "com.metrodata.common.dataaccess"})
@EntityScan(basePackages = {"com.metrodata.restaurant.service.dataaccess",
                                    "com.metrodata.common.dataaccess"})
@SpringBootApplication(scanBasePackages = "com.metrodata")
public class RestaurantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}
