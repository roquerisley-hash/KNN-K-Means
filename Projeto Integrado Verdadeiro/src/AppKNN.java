import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AppKNN {
    public static final long SEMENTE = 42L;

    public static void main(String[] args) throws Exception {
        double[] novo = {15.0, 35.0, 80.0, 52.0};
        if (args.length != 0 && args.length != 4) {
            throw new IllegalArgumentException("Use: AppKNN [horas partidas gasto taxa], com ponto decimal.");
        }
        for (int i = 0; i < args.length; i++) {
            novo[i] = Double.parseDouble(args[i]);
        }
        if (!Double.isFinite(novo[0]) || novo[0] < 0 || novo[0] > 168
                || !Double.isFinite(novo[1]) || novo[1] < 0 || novo[1] != Math.rint(novo[1])
                || !Double.isFinite(novo[2]) || novo[2] < 0
                || !Double.isFinite(novo[3]) || novo[3] < 0 || novo[3] > 100) {
            throw new IllegalArgumentException("Confira os valores: horas de 0 a 168, partidas inteiras "
                    + "não negativas, gasto não negativo e vitória de 0 a 100.");
        }

        try (Connection conexao = DriverManager.getConnection("jdbc:h2:mem:knn", "sa", "")) {

            try (PreparedStatement stmt = conexao.prepareStatement("RUNSCRIPT FROM 'jogadores.sql'")) {
                stmt.execute();
            }
            JogadorDAO dao = new JogadorDAOJdbc(conexao);
            List<Jogador> jogadores = dao.listarTodos();
            if (jogadores.size() != 150) {
                throw new IllegalStateException("Esperados 150 jogadores; encontrados " + jogadores.size());
            }
            DivisaoDados divisao = new DivisaoDados(jogadores, SEMENTE);
            System.out.println("Jogadores carregados pelo DAO: " + jogadores.size());
            System.out.printf("Semente: %d | Treino: %d | Teste: %d%n", SEMENTE,
                    divisao.getTreino().size(), divisao.getTeste().size());
            ExperimentosKNN.executar(divisao, Path.of("resultados"));

            KNN knn = new KNN(divisao.getTreino(), ExperimentosKNN.K_ESCOLHIDO,
                    new DistanciaEuclidiana(), true);
            System.out.println("\nJogador novo (horas, partidas, gasto, vitória): " + Arrays.toString(novo));
            System.out.println("Perfil previsto: " + knn.classificar(novo));
            System.out.println("Vizinhos usados na votação:");
            for (KNN.Vizinho vizinho : knn.encontrarVizinhos(novo)) {
                Jogador jogador = vizinho.getJogador();
                System.out.printf(Locale.ROOT, "%d | %s | %s | distância=%.6f%n", jogador.getId(),
                        jogador.getApelido(), jogador.getPerfil(), vizinho.getDistancia());
            }
            System.out.println("\nArquivos de resultados em: " + Path.of("resultados").toAbsolutePath());
        }
    }
}
