package com.hexa.banco.service;

import com.hexa.banco.exception.ValidacaoException;
import com.hexa.banco.model.Conta;
import com.hexa.banco.repository.ContaRepository;

public class ServicoTransferencia {
    private ContaRepository repositorio;

    public ServicoTransferencia(ContaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(String numeroOrigem, String numeroDestino, double valor) {
        Conta contaOrigem = repositorio.buscarPorNumero(numeroOrigem);
        Conta contaDestino = repositorio.buscarPorNumero(numeroDestino);

        if (contaOrigem == null || contaDestino == null) {
            throw new ValidacaoException("Conta de origem/destino não encontrada.");
        }

        contaOrigem.sacar(valor);
        contaDestino.depositar(valor);

        repositorio.salvar(contaOrigem);
        repositorio.salvar(contaDestino);
    }
}
