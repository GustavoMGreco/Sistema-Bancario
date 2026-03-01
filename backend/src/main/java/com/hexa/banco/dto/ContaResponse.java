package com.hexa.banco.dto;

public record ContaResponse(
        String tipo,
        String numero,
        String agencia,
        String nomeCliente,
        double saldo
) {}

// nota: essa classe é um DTO (Data Transfer Object): classe simples que serve apenas para carregar dados entre http/JSON e o sistema interno
