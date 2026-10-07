package com.pedrocomper.barbearia.dto;

import java.time.LocalDateTime;

import com.pedrocomper.barbearia.model.Agendamento;
import com.pedrocomper.barbearia.model.StatusAgendamento;

public record AgendamentoResponse(
        Long id,
        UsuarioResponse usuario,
        ServicoResponse servico,
        LocalDateTime dataHora,
        StatusAgendamento status) {

    public static AgendamentoResponse de(Agendamento agendamento) {
        return new AgendamentoResponse(
                agendamento.getId(),
                UsuarioResponse.de(agendamento.getUsuario()),
                ServicoResponse.de(agendamento.getServico()),
                agendamento.getDataHora(),
                agendamento.getStatus());
    }
}
