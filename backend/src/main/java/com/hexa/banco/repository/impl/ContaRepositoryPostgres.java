package com.hexa.banco.repository.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hexa.banco.exception.ValidacaoException;
import com.hexa.banco.model.Cliente;
import com.hexa.banco.model.Conta;
import com.hexa.banco.model.ContaCorrente;
import com.hexa.banco.model.ContaPoupanca;
import com.hexa.banco.repository.ContaRepository;

public class ContaRepositoryPostgres implements ContaRepository{

    String url = "jdbc:postgresql://localhost:5432/SistemaBancario";
    String usuario = "postgres";
    String senha = "";

    @Override
    public void salvar(Conta conta) {
        String sql = "INSERT INTO contas (numero, agencia, tipo, nome_cliente, cpf_cliente, saldo, parametro_especifico) VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (numero) DO UPDATE SET saldo = EXCLUDED.saldo";

        String tipo;
        double parametroEspecifico;

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (conta instanceof ContaCorrente contaC) {
                tipo = "CORRENTE";
                parametroEspecifico = contaC.getLimiteChequeEspecial();
            } else if (conta instanceof ContaPoupanca contaP) {
                tipo = "POUPANCA";
                parametroEspecifico = contaP.getTaxaRendimento();
            } else {
                throw new ValidacaoException("Tipo de conta inválido.");
            }

            stmt.setString(1, conta.getNumero());
            stmt.setString(2, conta.getAgencia());
            stmt.setString(3, tipo);
            stmt.setString(4, conta.getDono().getNome());
            stmt.setString(5, conta.getDono().getCpf());
            stmt.setDouble(6, conta.getSaldo());
            stmt.setDouble(7, parametroEspecifico);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro de persistência no banco de dados: " + e.getMessage());
        }
    }

    @Override
    public Conta buscarPorNumero(String numero) {
        String sql = "SELECT * FROM contas WHERE numero = ?";

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numero);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearContaDoResultSet(rs);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na busca de conta: " + e.getMessage());
        }
    }

    @Override
    public List<Conta> listarTodos() {
        String sql = "SELECT * FROM contas ORDER BY numero ASC";

        List<Conta> contas = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();) {

            while (rs.next()) {
                contas.add(mapearContaDoResultSet(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na listagem das contas: " + e.getMessage());
        }

        return contas;
    }

    private Conta mapearContaDoResultSet(ResultSet rs) throws SQLException {
        String num = rs.getString("numero");
        String agencia = rs.getString("agencia");
        String tipo = rs.getString("tipo");
        String nome = rs.getString("nome_cliente");
        String cpf = rs.getString("cpf_cliente");
        double saldo = rs.getDouble("saldo");
        double param = rs.getDouble("parametro_especifico");

        Cliente cliente = new Cliente(cpf, nome);
        Conta contaEncontrada;

        if (tipo.equalsIgnoreCase("corrente")) {
            contaEncontrada = new ContaCorrente(num, agencia, cliente, param);
        } else if (tipo.equalsIgnoreCase("poupanca")) {
            contaEncontrada = new ContaPoupanca(num, agencia, cliente, param);
        } else {
            throw new ValidacaoException("Tipo de conta inválido.");
        }

        contaEncontrada.setSaldo(saldo);

        return contaEncontrada;
    }

}
