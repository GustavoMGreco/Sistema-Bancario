package com.hexa.banco.model;

import com.hexa.banco.exception.ValidacaoException;

public class ContaCorrente extends Conta {
    private double limiteChequeEspecial;

    public ContaCorrente(String numero, String agencia, Cliente dono, double limiteChequeEspecial) {
        super(numero, agencia, dono);

        if (limiteChequeEspecial < 0) {
            throw new IllegalArgumentException("Limite do cheque especial não pode ser negativo.");
        }

        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    public double getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    // nota: sempre usar this em atributos de classe para melhor legibilidade
    @Override
    public void sacar(double valor) throws ValidacaoException {

        if (valor < 0) {
            throw new ValidacaoException("Valor do saque deve ser positivo.");
        }

        double saldoTotal = this.saldo + this.limiteChequeEspecial;

        if (valor <= saldoTotal) {
            this.saldo -= valor;
        } else {
            throw new ValidacaoException("Saldo e limite insuficientes. Saldo total disponível: " + saldoTotal);
        }

    }
}
