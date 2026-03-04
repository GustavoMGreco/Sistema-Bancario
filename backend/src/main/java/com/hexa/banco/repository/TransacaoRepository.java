package com.hexa.banco.repository;

import com.hexa.banco.model.Transacao;

import java.util.List;

public interface TransacaoRepository {

    public void registrar(Transacao transacao);

    public List<Transacao> buscarExtrato(String numeroConta);

}
