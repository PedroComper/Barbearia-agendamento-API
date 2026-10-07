package com.pedrocomper.barbearia.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pedrocomper.barbearia.dto.ServicoRequest;
import com.pedrocomper.barbearia.exception.RecursoNaoEncontradoException;
import com.pedrocomper.barbearia.model.Servico;
import com.pedrocomper.barbearia.repository.ServicoRepository;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Transactional(readOnly = true)
    public List<Servico> listar() {
        return servicoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado"));
    }

    @Transactional
    public Servico cadastrar(ServicoRequest request) {
        Servico servico = new Servico();
        servico.setNome(request.nome());
        servico.setPreco(request.preco());
        return servicoRepository.save(servico);
    }
}
