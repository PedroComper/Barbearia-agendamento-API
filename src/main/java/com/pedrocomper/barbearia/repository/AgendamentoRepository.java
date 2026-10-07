package com.pedrocomper.barbearia.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedrocomper.barbearia.model.Agendamento;
import com.pedrocomper.barbearia.model.StatusAgendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    boolean existsByDataHoraAndStatus(LocalDateTime dataHora, StatusAgendamento status);
}
