package br.com.ecommerce.sac.dao.impl;

import br.com.ecommerce.sac.connection.ConexaoBD;
import br.com.ecommerce.sac.dao.ClienteDAO;
import br.com.ecommerce.sac.exception.SacException;
import br.com.ecommerce.sac.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação concreta do ClienteDAO utilizando JDBC com Oracle.
 */
public class ClienteDAOImpl implements ClienteDAO {

    private final Connection conexao;

    public ClienteDAOImpl() {
        this.conexao = ConexaoBD.getInstancia().getConexao();
    }

    @Override
    public void salvar(Cliente cliente) {
        if (cliente == null) {
            throw new SacException("SAC_CLI_NULL", "Cliente não pode ser nulo.");
        }
        if (cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new SacException("SAC_CLI_EMAIL", "E-mail do cliente é obrigatório.");
        }

        String sql = "INSERT INTO sac_clientes (nome, email, cpf, telefone) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"id"})) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getCpf());
            stmt.setString(4, cliente.getTelefone());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }
            System.out.println("[ClienteDAO] Cliente salvo: " + cliente);

        } catch (SQLException e) {
            throw new SacException("SAC_CLI_SAVE", "Erro ao salvar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM sac_clientes WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        } catch (SQLException e) {
            throw new SacException("SAC_CLI_FIND", "Erro ao buscar cliente: " + e.getMessage(), e);
        }

        throw new SacException("SAC_CLI_NOT_FOUND", "Cliente com ID " + id + " não encontrado.");
    }

    @Override
    public Cliente buscarPorEmail(String email) {
        String sql = "SELECT * FROM sac_clientes WHERE email = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        } catch (SQLException e) {
            throw new SacException("SAC_CLI_FIND", "Erro ao buscar cliente por email: " + e.getMessage(), e);
        }

        throw new SacException("SAC_CLI_NOT_FOUND", "Cliente com e-mail '" + email + "' não encontrado.");
    }

    @Override
    public void atualizar(Cliente cliente) {
        if (cliente == null || cliente.getId() <= 0) {
            throw new SacException("SAC_CLI_UPDATE", "Cliente inválido para atualização.");
        }

        String sql = "UPDATE sac_clientes SET nome = ?, email = ?, cpf = ?, telefone = ? WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getCpf());
            stmt.setString(4, cliente.getTelefone());
            stmt.setInt(5, cliente.getId());

            int linhas = stmt.executeUpdate();
            if (linhas == 0) {
                throw new SacException("SAC_CLI_NOT_FOUND", "Cliente ID " + cliente.getId() + " não encontrado.");
            }
            System.out.println("[ClienteDAO] Cliente atualizado: " + cliente);

        } catch (SQLException e) {
            throw new SacException("SAC_CLI_UPDATE", "Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM sac_clientes WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int linhas = stmt.executeUpdate();
            if (linhas == 0) {
                throw new SacException("SAC_CLI_NOT_FOUND", "Cliente ID " + id + " não encontrado.");
            }
            System.out.println("[ClienteDAO] Cliente ID " + id + " removido.");

        } catch (SQLException e) {
            throw new SacException("SAC_CLI_DELETE", "Erro ao deletar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM sac_clientes ORDER BY nome";
        List<Cliente> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            throw new SacException("SAC_CLI_LIST", "Erro ao listar clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setEmail(rs.getString("email"));
        c.setCpf(rs.getString("cpf"));
        c.setTelefone(rs.getString("telefone"));
        return c;
    }
}
