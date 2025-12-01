package com.hexa.banco.model;

import static org.junit.jupiter.api.Assertions.*;

import com.hexa.banco.exception.ValidacaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContaTest {

    private ContaCorrente contaC;
    private ContaPoupanca contaP;

    @BeforeEach
    void setUp() {
        Cliente cliente = new Cliente("111.222.333-44", "Gustavo");
        contaC = new ContaCorrente("01", "101", cliente, 1000);
        contaP = new ContaPoupanca("02", "101", cliente, 0.01);
    }

    @Test
    void testDepositarValorPositivo() {
        contaC.depositar(100.0);

        // assertEquals(esperado, atual, margem de erro para double)
        assertEquals(100.0, contaC.getSaldo(), 0.0001);
    }

    @Test
    void testDepositarValorNegativo() {
        assertThrows(ValidacaoException.class, () -> {
            contaC.depositar(-100);
        });
    }

    @Test
    void testSacarPoupancaComSaldo() {
        contaP.depositar(100.0);
        contaP.sacar(50.0);

        assertEquals(50, contaP.getSaldo(), 0.0001);
    }

    @Test
    void testSacarPoupancaSemSaldo() {
        // se eu não declarar o saldo inicial, ele é 0 por padrão
        // garante que a execução lance ValidacaoException, falhando o teste se o metodo rodar com sucesso
        // mais seguro que try-catch, pois reprova o teste automaticamente se a exceção não for lançada
        assertThrows(ValidacaoException.class, () -> {      // () -> { ... } Lambda
            contaP.sacar(1.0);
        });
    }

    @Test
    void testSacarCorrenteUsandoLimite() {
        contaC.sacar(500.0);

        assertEquals(-500.0, contaC.getSaldo(), 0.0001);
    }

    @Test
    void testSacarCorrenteSemSaldoLimite() {
        assertThrows(ValidacaoException.class, () -> {
            contaC.sacar(1100.0);
        });
    }

    @Test
    void testAplicarRendimento() {
        contaP.depositar(100.0);
        contaP.aplicarRendimento();

        assertEquals(101.0, contaP.getSaldo(), 0.0001);
    }
}
