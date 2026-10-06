package com.example.main.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.main.exception.RecursoNaoEncontradoException;
import com.example.main.model.Servico;
import com.example.main.repository.ServicoRepository;

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
    public Servico cadastrar(Servico servico) {
        return servicoRepository.save(servico);
    }
}
