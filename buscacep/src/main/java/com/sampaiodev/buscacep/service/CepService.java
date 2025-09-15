package com.sampaiodev.buscacep.service;

import com.sampaiodev.buscacep.client.ViaCepClient;
import com.sampaiodev.buscacep.model.CepResponse;
import com.sampaiodev.buscacep.model.LogConsulta;
import com.sampaiodev.buscacep.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CepService {

    private final ViaCepClient viaCepClient;
    private final LogRepository logRepository;

    public CepResponse buscarCep(String cep) {
        CepResponse response = viaCepClient.consultarCep(cep);
        logRepository.save(new LogConsulta(LocalDateTime.now(), cep, response));
        return response;
    }
}
