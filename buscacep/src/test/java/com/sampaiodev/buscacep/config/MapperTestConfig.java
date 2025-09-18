package com.sampaiodev.buscacep.config;

import com.sampaiodev.buscacep.mapper.CepMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MapperTestConfig {
    @Bean
    public CepMapper cepMapper() {
        return Mappers.getMapper(CepMapper.class);
    }
}