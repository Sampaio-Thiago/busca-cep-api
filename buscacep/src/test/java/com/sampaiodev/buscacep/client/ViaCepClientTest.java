package com.sampaiodev.buscacep.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sampaiodev.buscacep.BuscacepApplication;
import com.sampaiodev.buscacep.model.CepResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = BuscacepApplication.class)
@WireMockTest(httpPort = 8081)
class ViaCepClientTest {
    @Autowired
    private ViaCepClient viaCepClient;

    @Test
    void deveConsultarCepComFeignClient() {
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

        CepResponse response = viaCepClient.consultarCep("01001000");

        assertNotNull(response);
        assertEquals("Praça da Sé", response.getLogradouro());
        assertEquals("SP", response.getUf());
    }
}