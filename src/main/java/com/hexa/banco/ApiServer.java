package com.hexa.banco;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexa.banco.dto.ContaResponse;
import com.hexa.banco.dto.CriacaoContaRequest;
import com.hexa.banco.dto.TransferenciaRequest;
import com.hexa.banco.exception.ValidacaoException;
import com.hexa.banco.model.Cliente;
import com.hexa.banco.model.Conta;
import com.hexa.banco.model.ContaCorrente;
import com.hexa.banco.model.ContaPoupanca;
import com.hexa.banco.repository.ContaRepository;
import com.hexa.banco.repository.impl.ContaRepositoryPostgres;
import com.hexa.banco.service.ServicoTransferencia;

import java.util.List;

import static spark.Spark.*;

public class ApiServer {

    public static void main(String[] args) {

        ContaRepository contaRepo = new ContaRepositoryPostgres();
        ServicoTransferencia transferencia = new ServicoTransferencia(contaRepo);
        ObjectMapper mapper = new ObjectMapper();

        port(8080);

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        });

        exception(ValidacaoException.class, (exception, request, response) -> {
            response.status(400);
            response.type("application/json");  // estou dizendo para o navegador que o conteúdo é JSON
            response.body("{\"erro\": \"" + exception.getMessage() + "\"}");
        });

        get("/contas", ((request, response) -> {
            response.type("application/json");
            List<Conta> listaDeContas = contaRepo.listarTodos();
            List<ContaResponse> responses = listaDeContas.stream().map(ApiServer::paraResponse).toList();

            return mapper.writeValueAsString(responses);
        }));

        get("/contas/:numero", ((request, response) -> {
            response.type("application/json");

            String numero = request.params(":numero");
            Conta contaEncontrada = contaRepo.buscarPorNumero(numero);
            if (contaEncontrada == null) {
                response.status(404);
                return "{\"erro\": \"Conta não encontrada.\"}";
            }
            ContaResponse conversao = paraResponse(contaEncontrada);

            return mapper.writeValueAsString(conversao);
        }));

        post("/transferencias", ((request, response) -> {
            response.type("application/json");
            String texto = request.body();
            TransferenciaRequest dadosTransferencia = mapper.readValue(texto, TransferenciaRequest.class);
            transferencia.executar(
                    dadosTransferencia.numeroOrigem(),
                    dadosTransferencia.numeroDestino(),
                    dadosTransferencia.valor());
            response.status(200);

            return "{\"mensagem\": \"Transferência realizada com sucesso.\"}";
        }));

        post("/contas", ((request, response) -> {
            response.type("application/json");
            String texto = request.body();
            CriacaoContaRequest requisicao = mapper.readValue(texto, CriacaoContaRequest.class);
            Cliente cliente = new Cliente(requisicao.cpfCliente(), requisicao.nomeCliente());

            Conta novaConta;

            if (contaRepo.buscarPorNumero(requisicao.numero()) != null) {
                throw new ValidacaoException("Esse número de conta já está em uso");
            }

            if (requisicao.tipo().equalsIgnoreCase("CORRENTE")) {
                novaConta = new ContaCorrente(requisicao.numero(), requisicao.agencia(), cliente, requisicao.parametroEspecifico());
            } else if (requisicao.tipo().equalsIgnoreCase("POUPANCA")) {
                novaConta = new ContaPoupanca(requisicao.numero(), requisicao.agencia(), cliente, requisicao.parametroEspecifico());
            } else {
                throw new ValidacaoException("Tipo de conta inválido.");
            }

            contaRepo.salvar(novaConta);

            response.status(201);
            ContaResponse conversao = paraResponse(novaConta);

            return mapper.writeValueAsString(conversao);
        }));

        System.out.println("Servidor rodando em http://localhost:8080");
    }

    // estou convertendo uma Conta (em memória) para uma ContaResponse (que vai para a ‘internet’) ... (converter a Entidade para DTO)
    private static ContaResponse paraResponse(Conta conta) {
        return new ContaResponse(
                conta.getNumero(),
                conta.getAgencia(),
                conta.getDono().getNome(),
                conta.getSaldo()
        );
    }

}
