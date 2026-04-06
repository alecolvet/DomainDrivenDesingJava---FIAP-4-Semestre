package com.hospitalrafael.crm.repository;

import com.hospitalrafael.crm.connection.ConexaoBanco;
import com.hospitalrafael.crm.model.Lead;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositório de Leads.
 * Responsável por toda comunicação com a tabela LEAD via JDBC.
 */
public class LeadRepository {

    /**
     * Insere um novo lead no banco e retorna o objeto com ID gerado.
     */
    public Lead salvar(Lead lead) {
        String sql = "INSERT INTO LEAD (NOME, EMAIL, TELEFONE, CANAL_ORIGEM, STATUS, " +
                     "LEAD_SCORE, PRIORIDADE, FATOR_URGENCIA, PLANO_SAUDE, " +
                     "PROCEDIMENTO_INTERESSE, CRIADO_EM) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"ID"})) {

            stmt.setString(1, lead.getNome());
            stmt.setString(2, lead.getEmail());
            stmt.setLong(3, lead.getTelefone() != null ? lead.getTelefone() : 0);
            stmt.setString(4, lead.getCanalOrigem());
            stmt.setString(5, lead.getStatus() != null ? lead.getStatus() : "Novo");
            stmt.setString(6, lead.getLeadScore());
            stmt.setInt(7, lead.getPrioridade() != null ? lead.getPrioridade() : 4);
            stmt.setInt(8, Boolean.TRUE.equals(lead.getFatorUrgencia()) ? 1 : 0);
            stmt.setString(9, lead.getPlanoSaude());
            stmt.setString(10, lead.getProcedimentoInteresse());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) lead.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar lead: " + e.getMessage(), e);
        }

        return lead;
    }

    /**
     * Atualiza o status e o score de um lead existente.
     */
    public void atualizar(Lead lead) {
        String sql = "UPDATE LEAD SET STATUS = ?, LEAD_SCORE = ?, PRIORIDADE = ? WHERE ID = ?";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lead.getStatus());
            stmt.setString(2, lead.getLeadScore());
            stmt.setInt(3, lead.getPrioridade() != null ? lead.getPrioridade() : 4);
            stmt.setLong(4, lead.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar lead: " + e.getMessage(), e);
        }
    }

    /**
     * Busca um lead pelo ID.
     */
    public Optional<Lead> buscarPorId(Long id) {
        String sql = "SELECT * FROM LEAD WHERE ID = ?";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar lead: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * Retorna todos os leads ordenados por prioridade (maior prioridade primeiro).
     */
    public List<Lead> buscarTodosOrdenadosPorPrioridade() {
        String sql = "SELECT * FROM LEAD ORDER BY PRIORIDADE ASC";
        return executarConsulta(sql);
    }

    /**
     * Retorna apenas os leads marcados como urgentes, por prioridade.
     */
    public List<Lead> buscarUrgentes() {
        String sql = "SELECT * FROM LEAD WHERE FATOR_URGENCIA = 1 ORDER BY PRIORIDADE ASC";
        return executarConsulta(sql);
    }

    /**
     * Retorna leads filtrados por status.
     */
    public List<Lead> buscarPorStatus(String status) {
        String sql = "SELECT * FROM LEAD WHERE STATUS = ? ORDER BY PRIORIDADE ASC";
        List<Lead> lista = new ArrayList<>();

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar leads por status: " + e.getMessage(), e);
        }

        return lista;
    }

    /**
     * Verifica se já existe um lead com o e-mail informado.
     */
    public boolean existePorEmail(String email) {
        String sql = "SELECT COUNT(*) FROM LEAD WHERE EMAIL = ?";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar e-mail: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna o total de leads por status (relatório JDBC direto).
     */
    public void imprimirEstatisticas() {
        String sql = "SELECT STATUS, COUNT(*) AS TOTAL FROM LEAD GROUP BY STATUS ORDER BY STATUS";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("  Estatísticas de Leads (via JDBC):");
            while (rs.next()) {
                System.out.printf("    %-20s : %d%n", rs.getString("STATUS"), rs.getInt("TOTAL"));
            }
        } catch (SQLException e) {
            System.out.println("  [ERRO ao gerar estatísticas]: " + e.getMessage());
        }
    }

    // ─── Métodos auxiliares ───────────────────────────────────────────────────

    private List<Lead> executarConsulta(String sql) {
        List<Lead> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar leads: " + e.getMessage(), e);
        }
        return lista;
    }

    // ─── Mapeamento ResultSet → Lead ──────────────────────────────────────────

    private Lead mapear(ResultSet rs) throws SQLException {
        Lead lead = new Lead();
        lead.setId(rs.getLong("ID"));
        lead.setNome(rs.getString("NOME"));
        lead.setEmail(rs.getString("EMAIL"));
        lead.setTelefone(rs.getLong("TELEFONE"));
        lead.setCanalOrigem(rs.getString("CANAL_ORIGEM"));
        lead.setStatus(rs.getString("STATUS"));
        lead.setLeadScore(rs.getString("LEAD_SCORE"));
        lead.setPrioridade(rs.getInt("PRIORIDADE"));
        lead.setFatorUrgencia(rs.getInt("FATOR_URGENCIA") == 1);
        lead.setPlanoSaude(rs.getString("PLANO_SAUDE"));
        lead.setProcedimentoInteresse(rs.getString("PROCEDIMENTO_INTERESSE"));
        Date criadoEm = rs.getDate("CRIADO_EM");
        if (criadoEm != null) lead.setCriadoEm(criadoEm.toLocalDate());
        return lead;
    }
}
