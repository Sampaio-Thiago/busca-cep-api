package com.sampaiodev.buscacep.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sampaiodev.buscacep.domain.service.CepService;
import com.sampaiodev.buscacep.model.CepResponse;
import com.sampaiodev.buscacep.domain.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@ActiveProfiles("test")
@SpringBootTest
@WireMockTest(httpPort = 8081)
class CepServiceTest {

    @Autowired
    private CepService cepService;

    @Autowired
    private LogRepository logRepository;

    @BeforeEach
    void limparLogs() {
        logRepository.deleteAll();
    }

    @Test
    void deveConsultarCepERegistrarLog() {
        // Arrange: Stub da API externa
        stubFor(get(urlEqualTo("/ws/01001000/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                      "cep": "01001-000",
                      "logradouro": "Praça da Sé",
                      "bairro": "Sé",
                      "localidade": "São Paulo",
                      "uf": "SP"
                    }
                """)));

        // Act: Executa o serviço
        CepResponse response = cepService.buscarCep("01001000");

        // Assert: Valida o retorno
        assertEquals("Praça da Sé", response.getLogradouro(), "Logradouro não corresponde");
        assertEquals("São Paulo", response.getLocalidade(), "Localidade não corresponde");

        // Assert: Valida se o log foi persistido
        var logs = logRepository.findAll();
        assertEquals(1, logs.size(), "Deveria haver exatamente 1 log registrado");
        assertEquals("01001000", logs.get(0).getCep(), "CEP registrado no log está incorreto");
    }
}