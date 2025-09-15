package com.sampaiodev.buscacep.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sampaiodev.buscacep.BuscacepApplication;
import com.sampaiodev.buscacep.model.CepResponse;
import com.sampaiodev.buscacep.repository.LogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest(classes = BuscacepApplication.class)
@WireMockTest(httpPort = 8081)
class CepServiceTest {

    @Autowired
    private CepService cepService;

    @Autowired
    private LogRepository logRepository;

    @Test
    void deveConsultarCepERegistrarLog() {
        // Stub da API externa
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

        // Executa o serviço
        CepResponse response = cepService.buscarCep("01001000");

        // Valida o retorno
        assertEquals("Praça da Sé", response.getLogradouro());
        assertEquals("São Paulo", response.getLocalidade());

        // Valida se o log foi persistido
        assertTrue(logRepository.findAll().stream()
                .anyMatch(log -> log.getCep().equals("01001000")));
    }
}