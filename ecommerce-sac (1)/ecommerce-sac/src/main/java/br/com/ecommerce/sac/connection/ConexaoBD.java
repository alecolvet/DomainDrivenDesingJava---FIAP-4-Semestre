package br.com.ecommerce.sac.connection;

import br.com.ecommerce.sac.exception.SacException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Padrão Singleton — garante uma única instância de conexão com o banco Oracle.
 * Usado por todos os DAOs do módulo SAC.
 */
public class ConexaoBD {

    // ──────────────────────────────────────────────
    // Configurações de conexão — Servidor FIAP Oracle
    // ──────────────────────────────────────────────
    private static final String URL      = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl";
    private static final String USUARIO  = "rm560059";
    private static final String SENHA    = "fiap26";

    // Instância única (Singleton)
    private static ConexaoBD instancia;
    private Connection conexao;

    /** Construtor privado: proíbe instanciação externa. */
    private ConexaoBD() {
        try {
            this.conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            System.out.println("[ConexaoBD] Conexão estabelecida com sucesso!");
        } catch (SQLException e) {
            throw new SacException("SAC_DB_CONN", "Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna a instância única do Singleton.
     * Reconecta automaticamente se a conexão estiver fechada.
     */
    public static ConexaoBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoBD();
        }
        return instancia;
    }

    /**
     * Retorna o objeto Connection para uso nos DAOs.
     * Reconecta se necessário.
     */
    public Connection getConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {
                System.out.println("[ConexaoBD] Reconectando ao banco...");
                instancia = new ConexaoBD();
            }
        } catch (SQLException e) {
            throw new SacException("SAC_DB_CONN", "Erro ao verificar conexão: " + e.getMessage(), e);
        }
        return conexao;
    }

    /** Fecha a conexão com o banco. */
    public void fecharConexao() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("[ConexaoBD] Conexão encerrada.");
            }
        } catch (SQLException e) {
            throw new SacException("SAC_DB_CLOSE", "Erro ao fechar conexão: " + e.getMessage(), e);
        }
    }
}
