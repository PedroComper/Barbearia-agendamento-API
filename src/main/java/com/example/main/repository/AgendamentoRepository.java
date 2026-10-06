package com.example.main.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.main.model.Agendamento;
import com.example.main.model.StatusAgendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    boolean existsByDataHoraAndStatus(LocalDateTime dataHora, StatusAgendamento status);
}
