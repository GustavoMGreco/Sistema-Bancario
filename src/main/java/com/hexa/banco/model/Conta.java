package com.hexa.banco.model;

import com.hexa.banco.exception.ValidacaoException;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ContaCorrente.class, name = "CORRENTE"),
        @JsonSubTypes.Type(value = ContaPoupanca.class, name = "POUPANÇA")
})

public abstract class Conta {

    private String numero;
    private String agencia;
    private Cliente dono;
    protected double saldo;

    // nota: o construtor não deve receber o atributo saldo como parâmetro, apenas definir
    public Conta(String numero, String agencia, Cliente dono) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Número não pode ser nulo ou vazio.");
        }
        if (agencia == null || agencia.trim().isEmpty()) {
            throw new IllegalArgumentException("Agência não pode ser nula ou vazia.");
        }
        if (dono == null) {
            throw new IllegalArgumentException("Dono não pode ser nulo.");
        }

        this.numero = numero;
        this.agencia = agencia;
        this.dono = dono;
        this.saldo = 0.0;
    }

    public double getSaldo() {
        return saldo;
    }

    public void depositar(double valor) {
        if (valor <= 0) {
            throw new ValidacaoException("O valor deve ser maio que zero.");
        }
        this.saldo += valor;
    }

    // é abstrato pois cada tipo de conta (corrente, poupança) vai ter sua propria regra de saque
    public abstract void sacar(double valor) throws ValidacaoException;

    public String getNumero() {
        return numero;
    }

    public String getAgencia() {
        return agencia;
    }

    public Cliente getDono() {
        return dono;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return Objects.equals(numero, conta.numero) &&
                Objects.equals(agencia, conta.agencia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, agencia);
    }

    @Override
    public String toString() {
        return "Conta{" +
                "numero='" + numero + '\'' +
                ", agencia='" + agencia + '\'' +
                ", dono=" + dono.getNome() +
                ", saldo=" + saldo +
                '}';
    }

}