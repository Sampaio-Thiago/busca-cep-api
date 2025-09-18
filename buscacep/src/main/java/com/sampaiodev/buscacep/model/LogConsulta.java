package com.sampaiodev.buscacep.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogConsulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataHora;
    private String cep;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cep_response_id")
    private CepResponse response;
}
