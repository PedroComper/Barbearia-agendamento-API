package com.example.main.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record AgendamentoRequest(

        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "O serviço é obrigatório")
        Long servicoId,

        @NotNull(message = "A data e hora são obrigatórias")
        @Future(message = "A data e hora devem estar no futuro")
        LocalDateTime dataHora) {
}
