package br.com.ecommerce.sac.dao.impl;

import br.com.ecommerce.sac.connection.ConexaoBD;
import br.com.ecommerce.sac.dao.TicketDAO;
import br.com.ecommerce.sac.exception.SacException;
import br.com.ecommerce.sac.model.Ticket;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação concreta do TicketDAO utilizando JDBC com Oracle.
 * Aplica o padrão DAO — toda lógica de banco fica isolada aqui.
 */
public class TicketDAOImpl implements TicketDAO {

    private final Connection conexao;

    public TicketDAOImpl() {
        // Obtém a conexão via Singleton
        this.conexao = ConexaoBD.getInstancia().getConexao();
    }

    // ──────────────────────────────────────────────────────────────
    // CREATE
    // ──────────────────────────────────────────────────────────────
    @Override
    public void salvar(Ticket ticket) {
        if (ticket == null) {
            throw new SacException("SAC_TICKET_NULL", "Ticket não pode ser nulo.");
        }
        if (ticket.getTitulo() == null || ticket.getTitulo().isBlank()) {
            throw new SacException("SAC_TICKET_TITULO", "Título do ticket é obrigatório.");
        }

        String sql = "INSERT INTO sac_tickets (titulo, descricao, status, prioridade, cliente_id, data_criacao, data_atualizacao) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"id"})) {
            stmt.setString(1, ticket.getTitulo());
            stmt.setString(2, ticket.getDescricao());
            stmt.setString(3, ticket.getStatus());
            stmt.setString(4, ticket.getPrioridade());
            stmt.setInt(5, ticket.getClienteId());
            stmt.setTimestamp(6, Timestamp.valueOf(ticket.getDataCriacao()));
            stmt.setTimestamp(7, Timestamp.valueOf(ticket.getDataAtualizacao()));

            stmt.executeUpdate();

            // Recupera o ID gerado automaticamente
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ticket.setId(rs.getInt(1));
                }
            }
            System.out.println("[TicketDAO] Ticket salvo: " + ticket);

        } catch (SQLException e) {
            throw new SacException("SAC_TICKET_SAVE", "Erro ao salvar ticket: " + e.getMessage(), e);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // READ
    // ──────────────────────────────────────────────────────────────
    @Override
    public Ticket buscarPorId(int id) {
        String sql = "SELECT * FROM sac_tickets WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearTicket(rs);
                }
            }
        } catch (SQLException e) {
            throw new SacException("SAC_TICKET_FIND", "Erro ao buscar ticket por ID: " + e.getMessage(), e);
        }

        throw new SacException("SAC_TICKET_NOT_FOUND", "Ticket com ID " + id + " não encontrado.");
    }

    @Override
    public List<Ticket> buscarPorCliente(int clienteId) {
        String sql = "SELECT * FROM sac_tickets WHERE cliente_id = ? ORDER BY data_criacao DESC";
        List<Ticket> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new SacException("SAC_TICKET_FIND", "Erro ao buscar tickets do cliente: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Ticket> buscarPorStatus(String status) {
        String sql = "SELECT * FROM sac_tickets WHERE status = ? ORDER BY prioridade, data_criacao DESC";
        List<Ticket> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new SacException("SAC_TICKET_FIND", "Erro ao buscar tickets por status: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Ticket> listarTodos() {
        String sql = "SELECT * FROM sac_tickets ORDER BY data_criacao DESC";
        List<Ticket> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearTicket(rs));
            }
        } catch (SQLException e) {
            throw new SacException("SAC_TICKET_LIST", "Erro ao listar tickets: " + e.getMessage(), e);
        }
        return lista;
    }

    // ──────────────────────────────────────────────────────────────
    // UPDATE
    // ──────────────────────────────────────────────────────────────
    @Override
    public void atualizar(Ticket ticket) {
        if (ticket == null || ticket.getId() <= 0) {
            throw new SacException("SAC_TICKET_UPDATE", "Ticket inválido para atualização.");
        }

        ticket.setDataAtualizacao(LocalDateTime.now());

        String sql = "UPDATE sac_tickets SET titulo = ?, descricao = ?, status = ?, "
                   + "prioridade = ?, data_atualizacao = ? WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, ticket.getTitulo());
            stmt.setString(2, ticket.getDescricao());
            stmt.setString(3, ticket.getStatus());
            stmt.setString(4, ticket.getPrioridade());
            stmt.setTimestamp(5, Timestamp.valueOf(ticket.getDataAtualizacao()));
            stmt.setInt(6, ticket.getId());

            int linhas = stmt.executeUpdate();
            if (linhas == 0) {
                throw new SacException("SAC_TICKET_NOT_FOUND", "Ticket ID " + ticket.getId() + " não encontrado para atualização.");
            }
            System.out.println("[TicketDAO] Ticket atualizado: " + ticket);

        } catch (SQLException e) {
            throw new SacException("SAC_TICKET_UPDATE", "Erro ao atualizar ticket: " + e.getMessage(), e);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // DELETE
    // ──────────────────────────────────────────────────────────────
    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM sac_tickets WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int linhas = stmt.executeUpdate();
            if (linhas == 0) {
                throw new SacException("SAC_TICKET_NOT_FOUND", "Ticket ID " + id + " não encontrado para exclusão.");
            }
            System.out.println("[TicketDAO] Ticket ID " + id + " removido.");

        } catch (SQLException e) {
            throw new SacException("SAC_TICKET_DELETE", "Erro ao deletar ticket: " + e.getMessage(), e);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Helper — mapeia ResultSet para objeto Ticket
    // ──────────────────────────────────────────────────────────────
    private Ticket mapearTicket(ResultSet rs) throws SQLException {
        Ticket t = new Ticket();
        t.setId(rs.getInt("id"));
        t.setTitulo(rs.getString("titulo"));
        t.setDescricao(rs.getString("descricao"));
        t.setStatus(rs.getString("status"));
        t.setPrioridade(rs.getString("prioridade"));
        t.setClienteId(rs.getInt("cliente_id"));
        t.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        t.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());
        return t;
    }
}
