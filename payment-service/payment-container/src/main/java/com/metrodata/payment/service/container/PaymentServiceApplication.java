package com.metrodata.payment.service.container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.metrodata.payment.service.dataaccess")
@EntityScan(basePackages = "com.metrodata.payment.service.dataaccess")
@SpringBootApplication(scanBasePackages = "com.metrodata")
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
