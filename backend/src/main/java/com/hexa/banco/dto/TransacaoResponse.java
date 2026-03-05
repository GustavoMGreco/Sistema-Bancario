package com.hexa.banco.dto;

public record TransacaoResponse(
        String contaOrigem,
        String contaDestino,
        double valor,
        String dataHora
) {
}
