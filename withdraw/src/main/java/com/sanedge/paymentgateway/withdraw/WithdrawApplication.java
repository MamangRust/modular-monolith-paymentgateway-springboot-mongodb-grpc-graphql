package com.sanedge.paymentgateway.withdraw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@SpringBootApplication(scanBasePackages = "com.sanedge.paymentgateway")
public class WithdrawApplication {

    public static void main(String[] args) {
        SpringApplication.run(WithdrawApplication.class, args);
    }
}
