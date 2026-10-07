package com.pedrocomper.barbearia.dto;

import java.math.BigDecimal;

import com.pedrocomper.barbearia.model.Servico;

public record ServicoResponse(Long id, String nome, BigDecimal preco) {

    public static ServicoResponse de(Servico servico) {
        return new ServicoResponse(servico.getId(), servico.getNome(), servico.getPreco());
    }
}
