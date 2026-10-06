package com.example.main.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.main.model.Servico;
import com.example.main.service.ServicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    public List<Servico> listar() {
        return servicoService.listar();
    }

    @PostMapping
    public ResponseEntity<Servico> cadastrar(@Valid @RequestBody Servico servico) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.cadastrar(servico));
    }
}
