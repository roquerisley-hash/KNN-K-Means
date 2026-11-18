import java.nio.file.Path;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TrabalhoFinal {
    public static void main(String[] args) throws Exception {
        String modo = args.length == 0 ? "todos" : args[0].toLowerCase(Locale.ROOT);
        if (!modo.equals("todos") && !modo.equals("a") && !modo.equals("b") && !modo.equals("c")) {
            throw new IllegalArgumentException("Use: TrabalhoFinal [todos|a|b|c] [horas partidas gasto taxa]");
        }
        String[] entrada = args.length == 0 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
        if ((modo.equals("a") || modo.equals("c")) && entrada.length != 0) {
            throw new IllegalArgumentException("O jogador novo só é usado na Parte B ou na execução completa.");
        }
        double[] novo = AppKNN.lerCaracteristicas(entrada);
        try (Connection conexao = BancoDados.abrir()) {
            JogadorDAO dao = new JogadorDAOJdbc(conexao);
            List<Jogador> jogadores = dao.listarTodos();
            if (modo.equals("todos") || modo.equals("a")) {
                App.executarConsultas(conexao);
            }
            if (modo.equals("todos") || modo.equals("b")) {
                AppKNN.executar(jogadores, novo, Path.of("resultados", "knn"));
            }
            if (modo.equals("todos") || modo.equals("c")) {
                ExperimentosKMeans.executar(jogadores, Path.of("resultados", "kmeans"));
            }
        }
    }
}
