package com.pedrocomper.barbearia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pedrocomper.barbearia.dto.ServicoRequest;
import com.pedrocomper.barbearia.dto.ServicoResponse;
import com.pedrocomper.barbearia.service.ServicoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Serviços", description = "Serviços oferecidos pela barbearia")
@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    public List<ServicoResponse> listar() {
        return servicoService.listar().stream().map(ServicoResponse::de).toList();
    }

    @PostMapping
    public ResponseEntity<ServicoResponse> cadastrar(@Valid @RequestBody ServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ServicoResponse.de(servicoService.cadastrar(request)));
    }
}
