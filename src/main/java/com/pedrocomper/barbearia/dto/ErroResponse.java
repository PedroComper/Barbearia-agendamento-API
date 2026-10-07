package com.pedrocomper.barbearia.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(int status, String erro, LocalDateTime timestamp, Map<String, String> campos) {
}
