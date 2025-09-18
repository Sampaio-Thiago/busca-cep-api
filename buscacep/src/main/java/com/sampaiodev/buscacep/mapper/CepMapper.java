package com.sampaiodev.buscacep.mapper;

import com.sampaiodev.buscacep.client.dto.response.CepResponseDTO;
import com.sampaiodev.buscacep.model.CepResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CepMapper {
    CepResponse toEntity(CepResponseDTO dto);
    CepResponseDTO toDto(CepResponse entity);
}