package com.sanedge.paymentgateway.topup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@SpringBootApplication(scanBasePackages = "com.sanedge.paymentgateway")
public class TopupApplication {

    public static void main(String[] args) {
        SpringApplication.run(TopupApplication.class, args);
    }
}
