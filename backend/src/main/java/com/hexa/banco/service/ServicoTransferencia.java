package com.hexa.banco.service;

import com.hexa.banco.exception.ValidacaoException;
import com.hexa.banco.model.Conta;
import com.hexa.banco.model.Transacao;
import com.hexa.banco.repository.ContaRepository;
import com.hexa.banco.repository.TransacaoRepository;

public class ServicoTransferencia {
    private ContaRepository contaRepo;
    private TransacaoRepository transacaoRepo;

    public ServicoTransferencia(ContaRepository contaRepo, TransacaoRepository transacaoRepo) {
        this.contaRepo = contaRepo;
        this.transacaoRepo = transacaoRepo;
    }

    public void executar(String numeroOrigem, String numeroDestino, double valor) {
        Conta contaOrigem = contaRepo.buscarPorNumero(numeroOrigem);
        Conta contaDestino = contaRepo.buscarPorNumero(numeroDestino);

        if (contaOrigem == null || contaDestino == null) {
            throw new ValidacaoException("Conta de origem/destino não encontrada.");
        }

        contaOrigem.sacar(valor);
        contaDestino.depositar(valor);

        contaRepo.salvar(contaOrigem);
        contaRepo.salvar(contaDestino);

        Transacao novaTransacao = new Transacao(numeroOrigem, numeroDestino, valor);
        transacaoRepo.registrar(novaTransacao);
    }
}
