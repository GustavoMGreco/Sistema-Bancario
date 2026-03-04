package com.hexa.banco.repository.impl;

import com.hexa.banco.exception.ValidacaoException;
import com.hexa.banco.model.*;
import com.hexa.banco.repository.TransacaoRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransacaoRepositoryPostgres implements TransacaoRepository {
    String url = "jdbc:postgresql://localhost:5432/SistemaBancario";
    String usuario = "postgres";
    String senha = "12345";

    @Override
    public void registrar(Transacao transacao) {
        String sql = "INSERT INTO transacoes (conta_origem, conta_destino, valor) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transacao.getContaOrigem());
            stmt.setString(2, transacao.getContaDestino());
            stmt.setDouble(3, transacao.getValor());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro no registro de transação: " + e.getMessage());
        }
    }

    @Override
    public List<Transacao> buscarExtrato(String numeroConta) {
        String sql = "SELECT * FROM transacoes WHERE conta_origem = ? OR conta_destino = ? ORDER BY data_hora DESC";

        List<Transacao> extrato = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroConta);
            stmt.setString(2, numeroConta);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    extrato.add(mapearTransacaoDoResultSet(rs));
                }
            }

            return extrato;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar extrato de transações: " + e.getMessage());
        }
    }

    private Transacao mapearTransacaoDoResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String contaOrigem = rs.getString("conta_origem");
        String contaDestino = rs.getString("conta_destino");
        double valor = rs.getDouble("valor");
        java.time.LocalDateTime dataHora = rs.getTimestamp("data_hora").toLocalDateTime();

        return new Transacao(id, contaOrigem, contaDestino, valor, dataHora);
    }
}
