package com.hexa.banco.dto;

import java.time.LocalDateTime;

public record TransacaoResponse(
        String contaOrigem,
        String contaDestino,
        double valor,
        String dataHora
) {
}
