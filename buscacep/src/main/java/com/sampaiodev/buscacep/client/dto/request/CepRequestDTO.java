package com.sampaiodev.buscacep.client.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class CepRequestDTO {

    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos numéricos")
    private String cep;
}
