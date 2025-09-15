package com.sampaiodev.buscacep.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogConsulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataHora;
    private String cep;

    @Embedded
    private CepResponse response;

    public LogConsulta(LocalDateTime dataHora, String cep, CepResponse response) {
        this.dataHora = dataHora;
        this.cep = cep;
        this.response = response;
    }

    private String toJson(CepResponse response) {
        try {
            return new ObjectMapper().writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
