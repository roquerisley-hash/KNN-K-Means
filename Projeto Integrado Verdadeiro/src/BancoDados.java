import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BancoDados {
    public static Connection abrir() throws SQLException {
        Connection conexao = DriverManager.getConnection("jdbc:h2:mem:grupo4", "sa", "");
        try {
            carregar(conexao);
            return conexao;
        } catch (SQLException e) {
            conexao.close();
            throw e;
        }
    }

    private static void carregar(Connection conexao) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement("RUNSCRIPT FROM 'jogadores.sql'")) {
            stmt.execute();
        }
        try (PreparedStatement stmt = conexao.prepareStatement("SELECT COUNT(*) FROM jogador");
             ResultSet rs = stmt.executeQuery()) {
            rs.next();
            int quantidade = rs.getInt(1);
            if (quantidade != 150) {
                throw new SQLException("Esperados 150 jogadores; encontrados " + quantidade);
            }
            System.out.println("Número de linhas: " + quantidade);
        }
    }
}
