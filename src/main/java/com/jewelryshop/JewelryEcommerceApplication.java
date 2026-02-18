package com.jewelryshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class JewelryEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JewelryEcommerceApplication.class, args);
        System.out.println("🚀 Jewelry E-Commerce Backend Started Successfully!");
    }
}
