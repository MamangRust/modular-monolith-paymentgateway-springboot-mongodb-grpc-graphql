package com.sanedge.paymentgateway.saldo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@SpringBootApplication(scanBasePackages = "com.sanedge.paymentgateway")
public class SaldoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaldoApplication.class, args);
    }
}
