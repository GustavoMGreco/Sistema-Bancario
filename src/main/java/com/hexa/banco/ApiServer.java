package com.hexa.banco;

import com.hexa.banco.exception.ValidacaoException;
import com.hexa.banco.model.Conta;
import com.hexa.banco.repository.ContaRepository;
import com.hexa.banco.repository.impl.ContaRepositoryEmMemoria;
import com.hexa.banco.service.ServicoPersistencia;
import com.hexa.banco.service.ServicoTransferencia;

import java.io.IOException;
import java.util.List;

import static spark.Spark.*;

public class ApiServer {

    private static ContaRepository contaRepo = new ContaRepositoryEmMemoria();
    private static ServicoPersistencia persistencia = new ServicoPersistencia();

    private static ServicoTransferencia transferencia = new ServicoTransferencia(contaRepo);

    public static void main(String[] args) {

        inicializarDados();

        port(8080);

        exception(ValidacaoException.class, (exception, request, response) -> {
            response.status(400);
            response.type("application/json");
            response.body("{\"erro\": \"" + exception.getMessage() + "\"}");
        });

        get("/teste", (req, res) -> "Olá, Banco Hexa! O sistema está online");

        System.out.println("Servidor rodando em http://localhost:8080");
    }

    private static void inicializarDados() {
        try {
            List<Conta> contasCarregadas = persistencia.carregar();
            for (Conta conta : contasCarregadas) {
                contaRepo.salvar(conta);
            }
            System.out.println("Backup carregado com sucesso. Total de contas: " + contasCarregadas.size());
        } catch (IOException e) {
            System.err.println("Aviso: Não foi possível carregar o arquivo de backup. Iniciando banco vazio.");
        }

    }

}
