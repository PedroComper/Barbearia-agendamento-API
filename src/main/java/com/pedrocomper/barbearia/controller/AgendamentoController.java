package com.pedrocomper.barbearia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pedrocomper.barbearia.dto.AgendamentoRequest;
import com.pedrocomper.barbearia.dto.AgendamentoResponse;
import com.pedrocomper.barbearia.service.AgendamentoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Agendamentos", description = "Marcação, consulta e cancelamento de horários")
@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @GetMapping
    public List<AgendamentoResponse> listar() {
        return agendamentoService.listar().stream().map(AgendamentoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public AgendamentoResponse buscarPorId(@PathVariable Long id) {
        return AgendamentoResponse.de(agendamentoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponse> cadastrar(@Valid @RequestBody AgendamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(AgendamentoResponse.de(agendamentoService.cadastrar(request)));
    }

    @PatchMapping("/{id}/cancelar")
    public AgendamentoResponse cancelar(@PathVariable Long id) {
        return AgendamentoResponse.de(agendamentoService.cancelar(id));
    }
}
