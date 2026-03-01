package com.hexa.banco.dto;

public record CriacaoContaRequest(
        String tipo,
        String numero,
        String agencia,
        String nomeCliente,
        String cpfCliente,
        double parametroEspecifico  // vai ser o limite se for corrente ou taxa se for poupança
) {
}

// nota: essa classe é um DTO (Data Transfer Object): classe simples que serve apenas para carregar dados entre http/JSON e o sistema interno