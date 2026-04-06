package com.hospitalrafael.crm.repository;

import com.hospitalrafael.crm.connection.ConexaoBanco;
import com.hospitalrafael.crm.model.Agendamento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositório de Agendamentos.
 * Responsável por toda comunicação com a tabela AGENDAMENTO via JDBC.
 */
public class AgendamentoRepository {

    /**
     * Insere um novo agendamento no banco e retorna com o ID gerado.
     */
    public Agendamento salvar(Agendamento agendamento) {
        String sql = "INSERT INTO AGENDAMENTO (LEAD_ID, OPERADOR_ID, PROCEDIMENTO, DATA_HORA, STATUS, CRIADO_EM) " +
                     "VALUES (?, ?, ?, ?, ?, SYSDATE)";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"ID"})) {

            stmt.setLong(1, agendamento.getLeadId());
            stmt.setLong(2, agendamento.getOperadorId());
            stmt.setString(3, agendamento.getProcedimento());
            stmt.setDate(4, Date.valueOf(agendamento.getDataHora()));
            stmt.setString(5, agendamento.getStatus() != null ? agendamento.getStatus() : "Pendente");
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) agendamento.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar agendamento: " + e.getMessage(), e);
        }

        return agendamento;
    }

    /**
     * Atualiza o status de um agendamento.
     */
    public void atualizarStatus(Long id, String novoStatus) {
        String sql = "UPDATE AGENDAMENTO SET STATUS = ? WHERE ID = ?";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar agendamento: " + e.getMessage(), e);
        }
    }

    /**
     * Busca um agendamento pelo ID.
     */
    public Optional<Agendamento> buscarPorId(Long id) {
        String sql = "SELECT * FROM AGENDAMENTO WHERE ID = ?";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agendamento: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * Retorna agendamentos de um lead específico.
     */
    public List<Agendamento> buscarPorLeadId(Long leadId) {
        String sql = "SELECT * FROM AGENDAMENTO WHERE LEAD_ID = ? ORDER BY DATA_HORA";
        List<Agendamento> lista = new ArrayList<>();

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, leadId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agendamentos: " + e.getMessage(), e);
        }

        return lista;
    }

    /**
     * Verifica se um operador já tem agendamento na data informada (conflito de horário).
     */
    public boolean existeConflito(Long operadorId, LocalDate dataHora) {
        String sql = "SELECT COUNT(*) FROM AGENDAMENTO WHERE OPERADOR_ID = ? AND DATA_HORA = ? AND STATUS != 'Cancelado'";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, operadorId);
            stmt.setDate(2, Date.valueOf(dataHora));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar conflito: " + e.getMessage(), e);
        }
    }

    // ─── Mapeamento ResultSet → Agendamento ───────────────────────────────────

    private Agendamento mapear(ResultSet rs) throws SQLException {
        Agendamento a = new Agendamento();
        a.setId(rs.getLong("ID"));
        a.setLeadId(rs.getLong("LEAD_ID"));
        a.setOperadorId(rs.getLong("OPERADOR_ID"));
        a.setProcedimento(rs.getString("PROCEDIMENTO"));
        Date dataHora = rs.getDate("DATA_HORA");
        if (dataHora != null) a.setDataHora(dataHora.toLocalDate());
        a.setStatus(rs.getString("STATUS"));
        Date criadoEm = rs.getDate("CRIADO_EM");
        if (criadoEm != null) a.setCriadoEm(criadoEm.toLocalDate());
        return a;
    }
}
