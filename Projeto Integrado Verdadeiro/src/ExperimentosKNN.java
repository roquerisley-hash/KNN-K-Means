import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class ExperimentosKNN {
    public static final int K_ESCOLHIDO = 5;

    public static void executar(DivisaoDados divisao, Path pasta) throws IOException {
        Files.createDirectories(pasta);
        salvarDivisao(divisao, pasta.resolve("divisao.csv"));
        try (PrintWriter resumo = escritor(pasta.resolve("acuracias.csv"));
             PrintWriter previsoes = escritor(pasta.resolve("previsoes.csv"));
             PrintWriter erros = escritor(pasta.resolve("erros.csv"));
             PrintWriter matrizes = escritor(pasta.resolve("matrizes.csv"))) {
            resumo.println("experimento;k;distancia;normalizacao;acertos;total;acuracia_percentual");
            String cabecalho = "configuracao;id;apelido;perfil_real;perfil_previsto;horas_semana;"
                    + "partidas_semana;gasto_mensal;taxa_vitoria";
            previsoes.println(cabecalho);
            erros.println(cabecalho);
            matrizes.println("configuracao;perfil_real;CASUAL;REGULAR;RANQUEADO");

            System.out.println("\nKNN: distância euclidiana com normalização min-max");
            for (int k : new int[] {1, 3, 5, 7, 9, 11}) {
                AvaliacaoKNN resultado = avaliar(divisao, k, new DistanciaEuclidiana(), true);
                registrarResumo(resumo, "variacao_k", k, "euclidiana", true, resultado);
            }

            System.out.println("\nComparações com K = " + K_ESCOLHIDO
                    + " (fixado antes da execução, sem escolher pelo teste)");
            MedidaDeDistancia[] medidas = {new DistanciaEuclidiana(), new DistanciaManhattan()};
            String[] nomes = {"euclidiana", "manhattan"};
            for (int i = 0; i < medidas.length; i++) {
                for (boolean normalizar : new boolean[] {true, false}) {
                    AvaliacaoKNN resultado = avaliar(divisao, K_ESCOLHIDO, medidas[i], normalizar);
                    registrarResumo(resumo, "comparacao", K_ESCOLHIDO, nomes[i], normalizar, resultado);
                    String configuracao = nomes[i] + (normalizar ? "_normalizada" : "_original");
                    registrarPrevisoes(previsoes, configuracao, resultado.getPrevisoes());
                    registrarPrevisoes(erros, configuracao, resultado.getErros());
                    registrarMatriz(matrizes, configuracao, resultado.getMatrizConfusao());
                    if (i == 0 && normalizar) {
                        mostrarMatriz(resultado.getMatrizConfusao());
                        System.out.println("Jogadores classificados incorretamente (modelo principal):");
                        for (AvaliacaoKNN.Previsao erro : resultado.getErros()) {
                            System.out.println(erro.getJogador() + " -> previsto: " + erro.getPerfilPrevisto());
                        }
                        if (resultado.getErros().isEmpty()) {
                            System.out.println("Nenhum nesta divisão de teste.");
                        }
                    }
                }
            }
        }
    }

    private static AvaliacaoKNN avaliar(DivisaoDados divisao, int k,
                                        MedidaDeDistancia medida, boolean normalizar) {
        KNN knn = new KNN(divisao.getTreino(), k, medida, normalizar);
        return new AvaliacaoKNN(knn, divisao.getTeste());
    }

    private static PrintWriter escritor(Path arquivo) throws IOException {
        return new PrintWriter(Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8));
    }

    private static void registrarResumo(PrintWriter arquivo, String experimento, int k,
                                         String medida, boolean normalizar, AvaliacaoKNN resultado) {
        double percentual = resultado.getAcuracia() * 100;
        int total = resultado.getPrevisoes().size();
        arquivo.printf(Locale.ROOT, "%s;%d;%s;%s;%d;%d;%.6f%n", experimento, k, medida,
                normalizar, resultado.getAcertos(), total, percentual);
        System.out.printf(Locale.ROOT, "K=%2d | %-10s | normalização=%-5s | %d/%d | %.2f%%%n",
                k, medida, normalizar, resultado.getAcertos(), total, percentual);
    }

    private static void registrarPrevisoes(PrintWriter arquivo, String configuracao,
                                            List<AvaliacaoKNN.Previsao> previsoes) {
        for (AvaliacaoKNN.Previsao previsao : previsoes) {
            Jogador j = previsao.getJogador();
            arquivo.printf(Locale.ROOT, "%s;%d;\"%s\";%s;%s;%.2f;%d;%.2f;%.2f%n", configuracao,
                    j.getId(), j.getApelido().replace("\"", "\"\""), j.getPerfil(),
                    previsao.getPerfilPrevisto(), j.getHorasSemana(), j.getPartidasSemana(),
                    j.getGastoMensal(), j.getTaxaVitoria());
        }
    }

    private static void registrarMatriz(PrintWriter arquivo, String configuracao, int[][] matriz) {
        for (Perfil perfil : Perfil.values()) {
            int[] linha = matriz[perfil.ordinal()];
            arquivo.printf("%s;%s;%d;%d;%d%n", configuracao, perfil, linha[0], linha[1], linha[2]);
        }
    }

    private static void mostrarMatriz(int[][] matriz) {
        System.out.println("Matriz principal: linhas = real; colunas = previsto");
        System.out.printf("%12s %8s %8s %10s%n", "", "CASUAL", "REGULAR", "RANQUEADO");
        for (Perfil perfil : Perfil.values()) {
            int[] linha = matriz[perfil.ordinal()];
            System.out.printf("%12s %8d %8d %10d%n", perfil, linha[0], linha[1], linha[2]);
        }
    }

    private static void salvarDivisao(DivisaoDados divisao, Path arquivo) throws IOException {
        try (PrintWriter saida = escritor(arquivo)) {
            saida.println("conjunto;id;perfil");
            for (Jogador jogador : divisao.getTreino()) {
                saida.printf("treino;%d;%s%n", jogador.getId(), jogador.getPerfil());
            }
            for (Jogador jogador : divisao.getTeste()) {
                saida.printf("teste;%d;%s%n", jogador.getId(), jogador.getPerfil());
            }
        }
    }
}
