package com.sampaiodev.buscacep.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.sampaiodev.buscacep.config.FeignTimeoutTestConfig;
import com.sampaiodev.buscacep.domain.repository.LogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CepControllerTest {

    private WireMockServer wireMockServer;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LogRepository logRepository;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8081);
        wireMockServer.start();
        configureFor("localhost", 8081);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Deve consultar o CEP com sucesso e salvar o log no banco")
    void shouldFetchCepAndSaveLog() throws Exception {
        logRepository.deleteAll();

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

        mockMvc.perform(MockMvcRequestBuilders.get("/cep/01001000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Praça da Sé"))
                .andExpect(jsonPath("$.uf").value("SP"));

        var logs = logRepository.findAll();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getCep()).isEqualTo("01001000");

    }

    @Test
    @DisplayName("Deve retornar 404 quando o CEP não existe na API externa")
    void shouldReturnNotFoundWhenCepDoesNotExist() throws Exception {
        stubFor(get(urlEqualTo("/ws/99999999/json"))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(MockMvcRequestBuilders.get("/cep/99999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 500 quando a API ViaCEP falha internamente")
    void shouldReturnInternalServerErrorWhenApiFails() throws Exception {
        stubFor(get(urlEqualTo("/ws/01001000/json"))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(MockMvcRequestBuilders.get("/cep/01001000"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Deve retornar 502 quando a API ViaCEP retorna JSON inválido")
    void shouldReturnBadGatewayWhenJsonIsInvalid() throws Exception {
        stubFor(get(urlEqualTo("/ws/01001000/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ cep: }")));

        mockMvc.perform(MockMvcRequestBuilders.get("/cep/01001000"))
                .andExpect(status().isBadGateway());
    }

    @Test
    @DisplayName("Deve retornar 504 quando a API ViaCEP demora e ocorre timeout")
    void shouldReturnGatewayTimeoutWhenApiIsSlow() throws Exception {
        System.out.println("WireMock ativo em: " + wireMockServer.baseUrl());
        stubFor(get(urlEqualTo("/ws/01001000/json"))
                .willReturn(aResponse()
                        .withFixedDelay(6000)));

        mockMvc.perform(MockMvcRequestBuilders.get("/cep/01001000"))
                .andExpect(status().isGatewayTimeout());
    }
}