import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws Exception {
        try (Connection conexao = BancoDados.abrir()) {
            executarConsultas(conexao);
        }
    }

    public static void executarConsultas(Connection conexao) throws SQLException {
        System.out.println("\nCONSULTA 1: jogadores por perfil\n");

        String sql = "SELECT COUNT(*) FROM jogador WHERE perfil = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            for (Perfil perfil : Perfil.values()) {
                stmt.setString(1, perfil.name());
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    System.out.println("Número de jogadores do perfil " + perfil.name() + ": " + rs.getInt(1));
                }
            }
        }

        System.out.println("\nCONSULTA 2: média de cada coluna numérica por perfil");

        sql = "SELECT perfil, AVG(horas_semana), AVG(partidas_semana), AVG(gasto_mensal), AVG(taxa_vitoria) FROM jogador GROUP BY perfil";

        System.out.println("\nPERFIL    | HORAS SEMANAIS | PARTIDAS SEMANAIS | GASTO MENSAL | TAXA DE VITÓRIAS |");
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.printf("%9s | %14.2f | %17.2f | %12.2f | %16.2f |\n", rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getDouble(4), rs.getDouble(5));
            }
        }

        System.out.println("\nCONSULTA 3: jogadores com mais de 20 horas semanais e taxa de vitórias acima de 55%, ordenados por taxa de vitórias");

        sql = "SELECT apelido, perfil, horas_semana, taxa_vitoria FROM jogador WHERE horas_semana > 20 AND taxa_vitoria > 55 ORDER BY taxa_vitoria DESC";

        System.out.println("\nAPELIDO          | PERFIL    | HORAS SEMANAIS | TAXA DE VITÓRIAS |");
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.printf("%16s | %9s | %14.2f | %16.2f |\n", rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4));
            }
        }
    }
}
