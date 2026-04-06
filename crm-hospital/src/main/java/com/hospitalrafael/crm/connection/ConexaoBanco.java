package com.hospitalrafael.crm.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável pela conexão com o banco de dados Oracle (FIAP).
 *
 * Substitua SEU_RM_AQUI pelo seu RM (ex: rm560059)
 * e SUA_SENHA_AQUI pela sua senha FIAP (ex: 15/03/2004)
 */
public class ConexaoBanco {

    private static final String URL     = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl";
    private static final String USUARIO = "rm560059";
    private static final String SENHA   = "fiap26";

    // Impede instanciação — classe utilitária
    private ConexaoBanco() {}

    /**
     * Abre e retorna uma conexão com o banco de dados Oracle.
     *
     * @return Connection ativa
     * @throws SQLException se as credenciais forem inválidas ou o banco estiver inacessível
     */
    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    /**
     * Testa se a conexão com o banco está funcionando.
     *
     * @return true se a conexão foi estabelecida com sucesso
     */
    public static boolean testarConexao() {
        try (Connection conn = getConexao()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.out.println("[ERRO DE CONEXAO] " + e.getMessage());
            return false;
        }
    }
}
