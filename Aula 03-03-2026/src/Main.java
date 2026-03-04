import java.sql.*;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws SQLException{
        String url = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
        String user = "rm560059";
        String password = "fiap26";

        String insert = "INSERT INTO ALUNO (ID, NOME, DOCUMENTO) VALUES (?, ?, ?)";
        String select = "SELECT * FROM ALUNO";

        Scanner entrada = new Scanner(System.in);

        System.out.println("Digite o nome: ");
        String nome = entrada.nextLine();

        System.out.println("Digite o documento: ");
        String documento = entrada.nextLine();

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
            preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setString(1, UUID.randomUUID().toString());
            preparedStatement.setString(2, nome);
            preparedStatement.setString(3, documento);
            preparedStatement.executeUpdate();

            System.out.println("Aluno incluido com sucesso!");

            preparedStatement = connection.prepareStatement(select);
            ResultSet resultado = preparedStatement.executeQuery();

            while (resultado.next()) {
                System.out.println("ID = " + resultado.getString("ID") + "NOME = " + resultado.getString("NOME") + "DOCUMENTO = " + resultado.getString("DOCUMENTO"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}