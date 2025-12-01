package com.hexa.banco.service;

import static org.junit.jupiter.api.Assertions.*;

import com.hexa.banco.exception.ValidacaoException;
import com.hexa.banco.model.Cliente;
import com.hexa.banco.model.ContaCorrente;
import com.hexa.banco.model.ContaPoupanca;
import com.hexa.banco.repository.impl.ContaRepositoryEmMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicoTransferenciaTest {

    ContaRepositoryEmMemoria contaRepo;
    ServicoTransferencia transferencia;
    ContaCorrente contaC;
    ContaPoupanca contaP;

    @BeforeEach
    void setUp() {
        contaRepo = new ContaRepositoryEmMemoria();
        transferencia = new ServicoTransferencia(contaRepo);
        Cliente cliente = new Cliente("111.222.333-44", "Gustavo");
        contaC = new ContaCorrente("01", "101", cliente, 1000);
        contaP = new ContaPoupanca("02", "101", cliente, 0.01);
        contaRepo.salvar(contaC);
        contaRepo.salvar(contaP);
    }

    @Test
    void testTransferenciaSucesso() {
        contaP.depositar(200);
        contaC.depositar(200);
        transferencia.executar("01", "02", 100);

        assertEquals(300, contaP.getSaldo(), 0.0001);
        assertEquals(100, contaC.getSaldo(), 0.0001);
    }

    @Test
    void contaNaoEncontrada() {
        contaC.depositar(100);

        assertThrows(ValidacaoException.class, () -> {
            transferencia.executar("01", "05", 100);
        });
    }

    @Test
    void saldoInsuficiente() {
        contaC.depositar(100); // mais o limite de 1000 fica 1100
        contaP.depositar(100);

        assertThrows(ValidacaoException.class, () -> {
            transferencia.executar("01", "02", 1200);
        });

        // garantir que o saldo da conta de destino não mudou
        assertEquals(100, contaP.getSaldo(), 0.0001);
    }
}
