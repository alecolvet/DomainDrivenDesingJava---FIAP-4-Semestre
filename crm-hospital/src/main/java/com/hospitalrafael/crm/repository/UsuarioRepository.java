package com.hospitalrafael.crm.repository;

import com.hospitalrafael.crm.connection.ConexaoBanco;
import com.hospitalrafael.crm.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositório de Usuarios.
 * Responsável por toda comunicação com a tabela USUARIO via JDBC.
 */
public class UsuarioRepository {

    /**
     * Insere um novo usuário no banco e retorna o objeto com ID gerado.
     */
    public Usuario salvar(Usuario usuario) {
        String sql = "INSERT INTO USUARIO (NOME, EMAIL, SENHA, DOC, TELEFONE, DATANASC) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"ID"})) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getDoc());
            stmt.setLong(5, usuario.getTelefone());
            stmt.setDate(6, Date.valueOf(usuario.getDataNasc()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) usuario.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário: " + e.getMessage(), e);
        }

        return usuario;
    }

    /**
     * Busca um usuário pelo ID.
     */
    public Optional<Usuario> buscarPorId(Long id) {
        String sql = "SELECT * FROM USUARIO WHERE ID = ?";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * Atualiza os dados de um usuário existente.
     */
    public void atualizar(Usuario usuario) {
        String sql = "UPDATE USUARIO SET NOME = ?, EMAIL = ?, SENHA = ?, TELEFONE = ? WHERE ID = ?";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setLong(4, usuario.getTelefone());
            stmt.setLong(5, usuario.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna todos os usuários cadastrados.
     */
    public List<Usuario> buscarTodos() {
        String sql = "SELECT * FROM USUARIO ORDER BY NOME";
        List<Usuario> lista = new ArrayList<>();

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }

        return lista;
    }

    /**
     * Verifica se já existe um usuário com o e-mail informado.
     */
    public boolean existePorEmail(String email) {
        String sql = "SELECT COUNT(*) FROM USUARIO WHERE EMAIL = ?";

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
     * Verifica se já existe um usuário com o CPF (doc) informado.
     */
    public boolean existePorDoc(String doc) {
        String sql = "SELECT COUNT(*) FROM USUARIO WHERE DOC = ?";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doc);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar CPF: " + e.getMessage(), e);
        }
    }

    // ─── Mapeamento ResultSet → Usuario ───────────────────────────────────────

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("ID"));
        u.setNome(rs.getString("NOME"));
        u.setEmail(rs.getString("EMAIL"));
        u.setSenha(rs.getString("SENHA"));
        u.setDoc(rs.getString("DOC"));
        u.setTelefone(rs.getLong("TELEFONE"));
        Date dataNasc = rs.getDate("DATANASC");
        if (dataNasc != null) u.setDataNasc(dataNasc.toLocalDate());
        return u;
    }
}
