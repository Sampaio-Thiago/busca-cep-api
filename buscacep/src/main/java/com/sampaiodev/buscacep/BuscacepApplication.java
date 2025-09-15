package com.sampaiodev.buscacep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.sampaiodev.buscacep.client")
@SpringBootApplication
public class BuscacepApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuscacepApplication.class, args);
    }
}
