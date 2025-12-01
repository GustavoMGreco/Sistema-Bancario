package com.hexa.banco.model;

import com.hexa.banco.exception.ValidacaoException;

public class ContaPoupanca extends Conta {

    private double taxaRendimento;

    public ContaPoupanca(String numero, String agencia, Cliente dono, double taxaRendimento) {
        super(numero, agencia, dono);

        if (taxaRendimento < 0) {
            throw new IllegalArgumentException("Taxa de rendimento não pode ser negativa.");
        }

        this.taxaRendimento = taxaRendimento;
    }

    public double getTaxaRendimento() {
        return taxaRendimento;
    }

    // nota: sempre usar this em atributos de classe para melhor legibilidade
    @Override
    public void sacar(double valor) throws ValidacaoException {

        if (valor < 0) {
            throw new ValidacaoException("Valor do saque deve ser positivo.");
        }

        if (valor <= this.saldo) {
            this.saldo -= valor;
        } else {
            throw new ValidacaoException("Saldo insuficiente.");
        }
    }

    public void aplicarRendimento() {
        double rendimento = this.saldo * this.taxaRendimento;
        this.saldo += rendimento;
    }

}
