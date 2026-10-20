package com.sanedge.paymentgateway.role;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@SpringBootApplication(scanBasePackages = "com.sanedge.paymentgateway")
public class RoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoleApplication.class, args);
    }
}
