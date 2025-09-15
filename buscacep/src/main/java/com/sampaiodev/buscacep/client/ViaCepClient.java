package com.sampaiodev.buscacep.client;

import com.sampaiodev.buscacep.model.CepResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "viacep", url = "${api.base.url}")
public interface ViaCepClient {

    @GetMapping("/ws/{cep}/json")
    CepResponse consultarCep(@PathVariable("cep") String cep);
}