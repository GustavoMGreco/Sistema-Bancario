package com.hexa.banco.dto;

public record TransferenciaRequest(
        String numeroOrigem,
        String numeroDestino,
        double valor
) {}

// nota: essa classe é um DTO (Data Transfer Object): classe simples que serve apenas para carregar dados entre http/JSON e o sistema interno
