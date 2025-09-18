package com.sampaiodev.buscacep.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.sampaiodev.buscacep.client.dto.response.CepResponseDTO;
import com.sampaiodev.buscacep.config.FeignTimeoutTestConfig;
import com.sampaiodev.buscacep.config.MapperTestConfig;
import com.sampaiodev.buscacep.mapper.CepMapper;
import com.sampaiodev.buscacep.model.CepResponse;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RestClientTest(ViaCepClient.class)
@Import({FeignAutoConfiguration.class, FeignTimeoutTestConfig.class, MapperTestConfig.class})
class ViaCepClientTest {

    private WireMockServer wireMockServer;

    @Autowired
    private ViaCepClient viaCepClient;

    @Autowired
    private CepMapper cepMapper;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8081);
        wireMockServer.start();
        configureFor("localhost", 8081);

        stubFor(get(urlEqualTo("/ws/01001000/json"))
                .willReturn(aResponse()
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
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Deve consultar o CEP com sucesso usando Feign Client")
    void shouldFetchCepSuccessfully() {
        CepResponseDTO dto = viaCepClient.consultarCep("01001000");
        CepResponse response = cepMapper.toEntity(dto);

        assertNotNull(response);
        assertEquals("Praça da Sé", response.getLogradouro());
        assertEquals("SP", response.getUf());

        verify(getRequestedFor(urlEqualTo("/ws/01001000/json")));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o CEP não existe (404)")
    void shouldThrowExceptionWhenCepNotFound() {
        stubFor(get(urlEqualTo("/ws/99999999/json"))
                .willReturn(aResponse().withStatus(404)));

        assertThrows(FeignException.NotFound.class, () -> {
            viaCepClient.consultarCep("99999999");
        });

        verify(getRequestedFor(urlEqualTo("/ws/99999999/json")));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a API ViaCEP retorna erro interno (500)")
    void shouldThrowExceptionWhenApiFails() {
        stubFor(get(urlEqualTo("/ws/01001000/json"))
                .willReturn(aResponse().withStatus(500)));

        assertThrows(FeignException.InternalServerError.class, () -> {
            viaCepClient.consultarCep("01001000");
        });

        verify(getRequestedFor(urlEqualTo("/ws/01001000/json")));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o JSON retornado está inválido")
    void shouldThrowExceptionWhenJsonIsInvalid() {
        stubFor(get(urlEqualTo("/ws/01001000/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ cep: }"))); // JSON inválido

        assertThrows(FeignException.class, () -> {
            viaCepClient.consultarCep("01001000");
        });

        verify(getRequestedFor(urlEqualTo("/ws/01001000/json")));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a API demora e ocorre timeout")
    void shouldThrowExceptionOnTimeout() {
        stubFor(get(urlEqualTo("/ws/01001000/json"))
                .willReturn(aResponse()
                        .withFixedDelay(5000))); // simula lentidão

        assertThrows(FeignException.class, () -> {
            viaCepClient.consultarCep("01001000");
        });

        verify(getRequestedFor(urlEqualTo("/ws/01001000/json")));
    }
}