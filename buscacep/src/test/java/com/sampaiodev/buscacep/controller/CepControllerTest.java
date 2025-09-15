package com.sampaiodev.buscacep.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sampaiodev.buscacep.BuscacepApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(classes = BuscacepApplication.class)
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8081)
class CepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarCepMockadoViaController() throws Exception {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/ws/01001000/json"))
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

        mockMvc.perform(get("/cep/01001000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Praça da Sé"))
                .andExpect(jsonPath("$.uf").value("SP"));
    }
}