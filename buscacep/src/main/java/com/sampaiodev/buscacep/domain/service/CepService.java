package com.sampaiodev.buscacep.domain.service;

import com.sampaiodev.buscacep.client.ViaCepClient;
import com.sampaiodev.buscacep.client.dto.response.CepResponseDTO;
import com.sampaiodev.buscacep.domain.exceptions.ExternalApiException;
import com.sampaiodev.buscacep.mapper.CepMapper;
import com.sampaiodev.buscacep.model.CepResponse;
import com.sampaiodev.buscacep.model.LogConsulta;
import com.sampaiodev.buscacep.domain.repository.LogRepository;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class CepService {
    @Value("${api.base.url}")
    private String apiBaseUrl;

    private final ViaCepClient viaCepClient;
    private final LogRepository logRepository;
    private final CepMapper cepMapper;

    public CepService(ViaCepClient viaCepClient, CepMapper cepMapper, LogRepository logRepository) {
        this.viaCepClient = viaCepClient;
        this.cepMapper = cepMapper;
        this.logRepository = logRepository;
    }

    @PostConstruct
    public void logApiUrl() {
        System.out.println("Feign apontando para: " + apiBaseUrl);
    }

    public CepResponse buscarCep(String cep) {
        try {
            System.out.println("Simulando timeout...");

            // Simula uma chamada que ultrapassa o tempo limite
            Thread.sleep(3000); // maior que o readTimeout configurado no Feign

            // Simula a exceção que o Feign lançaria em caso de timeout
            throw new FeignException.FeignClientException(
                    -1, // status típico de timeout
                    "Timeout simulado",
                    null,
                    null, null
            );

        } catch (FeignException e) {
            logRepository.save(LogConsulta.builder()
                    .dataHora(LocalDateTime.now())
                    .cep(cep)
                    .response(null)
                    .build());
            System.out.println("Feign status: " + e.status());

            throw new ExternalApiException("Erro ao consultar API ViaCEP", e.status());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalApiException("Erro de interrupção", 500);
        }
    }
}