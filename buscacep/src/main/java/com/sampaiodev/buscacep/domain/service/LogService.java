package com.sampaiodev.buscacep.domain.service;

import com.sampaiodev.buscacep.model.LogConsulta;
import com.sampaiodev.buscacep.domain.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    public List<LogConsulta> listarLogs() {
        return logRepository.findAll();
    }
}