package com.hexa.banco;

import static org.junit.jupiter.api.Assertions.*;

import com.hexa.banco.exception.ValidacaoException;
import com.hexa.banco.model.Cliente;
import com.hexa.banco.model.ContaCorrente;
import com.hexa.banco.model.ContaPoupanca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContaTest {

    private ContaCorrente cc;
    private ContaPoupanca cp;

    @BeforeEach
    void setUp() {
        Cliente cliente = new Cliente("111.222.333-44", "Gustavo");
        cc = new ContaCorrente("01", "101", cliente, 1000);
        cp = new ContaPoupanca("02", "101", cliente, 0.01);
    }

    @Test
    void testDepositarValorPositivo() {
        cc.depositar(100.0);

        // assertEquals(esperado, atual, margem de erro para double)
        assertEquals(100.0, cc.getSaldo(), 0.0001);
    }

    @Test
    void testDepositarValorNegativo() {
        assertThrows(ValidacaoException.class, () -> {
            cc.depositar(-100);
        });
    }

    @Test
    void testSacarPoupancaComSaldo() {
        cp.depositar(100.0);
        cp.sacar(50.0);

        assertEquals(50, cp.getSaldo(), 0.0001);
    }

    @Test
    void testSacarPoupancaSemSaldo() {
        // se eu não declarar o saldo inicial, ele é 0 por padrão
        // garante que a execução lance ValidacaoException, falhando o teste se o metodo rodar com sucesso
        // mais seguro que try-catch, pois reprova o teste automaticamente se a exceção não for lançada
        assertThrows(ValidacaoException.class, () -> {      // () -> { ... } Lambda
            cp.sacar(1.0);
        });
    }

    @Test
    void testSacarCorrenteUsandoLimite() {
        cc.sacar(500.0);

        assertEquals(-500.0, cc.getSaldo(), 0.0001);
    }

    @Test
    void testSacarCorrenteSemSaldoLimite() {
        assertThrows(ValidacaoException.class, () -> {
            cc.sacar(1100.0);
        });
    }

    @Test
    void testAplicarRendimento() {
        cp.depositar(100.0);
        cp.aplicarRendimento();

        assertEquals(101.0, cp.getSaldo(), 0.0001);
    }
}
