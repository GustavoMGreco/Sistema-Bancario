package com.hexa.banco;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexa.banco.dto.ContaResponse;
import com.hexa.banco.dto.TransferenciaRequest;
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

    public static void main(String[] args) {

        ContaRepository contaRepo = new ContaRepositoryEmMemoria();
        ServicoPersistencia persistencia = new ServicoPersistencia();
        ServicoTransferencia transferencia = new ServicoTransferencia(contaRepo);
        ObjectMapper mapper = new ObjectMapper();

        inicializarDados(contaRepo, persistencia);

        port(8080);

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
            persistencia.salvar(contaRepo.listarTodos());
            response.status(200);

            return "{\"mensagem\": \"Transferência realizada com sucesso.\"}";
        }));

        System.out.println("Servidor rodando em http://localhost:8080");
    }

    private static void inicializarDados(ContaRepository contaRepo, ServicoPersistencia persistencia) {
        try {
            List<Conta> contasCarregadas = persistencia.carregar();
            for (Conta conta : contasCarregadas) {
                contaRepo.salvar(conta);
            }
            System.out.println("Backup carregado com sucesso. Total de contas: " + contasCarregadas.size());
        } catch (IOException e) {
            System.err.println("Aviso: Não foi possível carregar o arquivo de backup. Motivo: " + e.getMessage());
            System.err.println("Iniciando banco vazio.");
        }

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
