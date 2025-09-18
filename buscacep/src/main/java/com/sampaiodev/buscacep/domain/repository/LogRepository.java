package com.sampaiodev.buscacep.domain.repository;

import com.sampaiodev.buscacep.model.LogConsulta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogConsulta, Long> {
}
