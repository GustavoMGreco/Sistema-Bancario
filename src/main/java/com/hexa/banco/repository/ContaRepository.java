package com.hexa.banco.repository;

import com.hexa.banco.model.Conta;

import java.util.List;

public interface ContaRepository {

    public void salvar(Conta conta);

    public Conta buscarPorNumero(String numero);

    public List<Conta> listarTodos();

}
