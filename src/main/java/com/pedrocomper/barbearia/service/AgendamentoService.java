package com.pedrocomper.barbearia.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pedrocomper.barbearia.dto.AgendamentoRequest;
import com.pedrocomper.barbearia.exception.RecursoNaoEncontradoException;
import com.pedrocomper.barbearia.exception.RegraNegocioException;
import com.pedrocomper.barbearia.model.Agendamento;
import com.pedrocomper.barbearia.model.Servico;
import com.pedrocomper.barbearia.model.StatusAgendamento;
import com.pedrocomper.barbearia.model.Usuario;
import com.pedrocomper.barbearia.repository.AgendamentoRepository;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioService usuarioService;
    private final ServicoService servicoService;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              UsuarioService usuarioService,
                              ServicoService servicoService) {
        this.agendamentoRepository = agendamentoRepository;
        this.usuarioService = usuarioService;
        this.servicoService = servicoService;
    }

    @Transactional(readOnly = true)
    public List<Agendamento> listar() {
        return agendamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado"));
    }

    @Transactional
    public Agendamento cadastrar(AgendamentoRequest request) {
        Usuario usuario = usuarioService.buscarPorId(request.usuarioId());
        Servico servico = servicoService.buscarPorId(request.servicoId());

        if (agendamentoRepository.existsByDataHoraAndStatus(request.dataHora(), StatusAgendamento.AGENDADO)) {
            throw new RegraNegocioException("Horário indisponível");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setUsuario(usuario);
        agendamento.setServico(servico);
        agendamento.setDataHora(request.dataHora());

        return agendamentoRepository.save(agendamento);
    }

    @Transactional
    public Agendamento cancelar(Long id) {
        Agendamento agendamento = buscarPorId(id);

        if (agendamento.getStatus() != StatusAgendamento.AGENDADO) {
            throw new RegraNegocioException("Apenas agendamentos ativos podem ser cancelados");
        }

        agendamento.setStatus(StatusAgendamento.CANCELADO);
        return agendamento;
    }
}
