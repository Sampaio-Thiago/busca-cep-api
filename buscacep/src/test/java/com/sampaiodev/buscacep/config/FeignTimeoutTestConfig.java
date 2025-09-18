package com.sampaiodev.buscacep.config;

import feign.Request;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class FeignTimeoutTestConfig {

    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(1000, 1000); // 1s connect + 1s read timeout
    }
}
