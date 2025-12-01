package com.hexa.banco.repository.impl;

import com.hexa.banco.model.Conta;
import com.hexa.banco.repository.ContaRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContaRepositoryEmMemoria implements ContaRepository {

    // Map<chave(identificador unico, nesse caso, o numero da conta [String]), valor(objeto completo [Conta])
    private Map<String, Conta> dados = new HashMap<>();

    @Override
    public void salvar(Conta conta) {
        dados.put(conta.getNumero(), conta);
    }

    @Override
    public Conta buscarPorNumero(String numero) {
        return dados.get(numero);
    }

    @Override
    public List<Conta> listarTodos() {
        // o metodo .values() retorna todas as contas como uma Collection, mas listarTodos pede uma List
        return new ArrayList<>(dados.values());
    }

}