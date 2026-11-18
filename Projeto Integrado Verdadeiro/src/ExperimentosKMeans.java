import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class ExperimentosKMeans {
    private static final long[] SEMENTES = {7, 21, 42, 84, 123};
    private static final int LIMITE_ITERACOES = 100;

    public static void executar(List<Jogador> jogadores, Path pasta) throws IOException {
        Files.createDirectories(pasta);
        NormalizadorMinMax normalizador = new NormalizadorMinMax();
        normalizador.encontrarMinMax(jogadores);
        double[][] originais = new double[jogadores.size()][];
        double[][] normalizados = new double[jogadores.size()][];
        for (int i = 0; i < jogadores.size(); i++) {
            originais[i] = jogadores.get(i).getCaracteristicas();
            normalizados[i] = normalizador.normalizar(originais[i]);
        }
        try (PrintWriter tentativas = escritor(pasta.resolve("inicializacoes.csv"));
             PrintWriter cotovelo = escritor(pasta.resolve("cotovelo.csv"));
             PrintWriter resumo = escritor(pasta.resolve("resumo_k3.csv"))) {
            tentativas.println("normalizacao;k;semente;inercia;iteracoes;convergiu");
            cotovelo.println("k;inercia;semente;iteracoes;convergiu");
            resumo.println("normalizacao;semente;inercia;pureza;iteracoes;convergiu");
            double[] inercias = new double[6];
            for (int k = 1; k <= 6; k++) {
                KMeans.Resultado melhor = melhorInicializacao(normalizados, k, true, tentativas);
                inercias[k - 1] = melhor.getInercia();
                cotovelo.printf(Locale.ROOT, "%d;%.10f;%d;%d;%s%n", k, melhor.getInercia(),
                        melhor.getSemente(), melhor.getIteracoes(), melhor.isConvergiu());
                if (k == 3) {
                    salvarK3(jogadores, melhor, normalizador, true, pasta, resumo);
                }
            }
            KMeans.Resultado semNormalizar = melhorInicializacao(originais, 3, false, tentativas);
            salvarK3(jogadores, semNormalizar, normalizador, false, pasta, resumo);
            GraficoCotovelo.salvar(inercias, pasta.resolve("cotovelo.png"));
            if (tentativas.checkError() || cotovelo.checkError() || resumo.checkError()) {
                throw new IOException("Falha ao gravar os resultados do K-Means.");
            }
        }
    }

    private static KMeans.Resultado melhorInicializacao(double[][] dados, int k,
                                                         boolean normalizar, PrintWriter saida) {
        KMeans.Resultado melhor = null;
        for (long semente : SEMENTES) {
            KMeans.Resultado atual = new KMeans(k, LIMITE_ITERACOES, semente).executar(dados);
            saida.printf(Locale.ROOT, "%s;%d;%d;%.10f;%d;%s%n", normalizar, k, semente,
                    atual.getInercia(), atual.getIteracoes(), atual.isConvergiu());
            if (melhor == null || atual.getInercia() < melhor.getInercia()) {
                melhor = atual;
            }
        }
        return melhor;
    }

    private static void salvarK3(List<Jogador> jogadores, KMeans.Resultado resultado,
                                  NormalizadorMinMax normalizador, boolean normalizar,
                                  Path pasta, PrintWriter resumo) throws IOException {
        int[][] tabela = contarPerfis(jogadores, resultado.getGrupos(), 3);
        double pureza = calcularPureza(tabela);
        resumo.printf(Locale.ROOT, "%s;%d;%.10f;%.10f;%d;%s%n", normalizar,
                resultado.getSemente(), resultado.getInercia(), pureza,
                resultado.getIteracoes(), resultado.isConvergiu());
        String sufixo = normalizar ? "normalizado" : "original";
        salvarGrupos(tabela, pasta.resolve("grupos_" + sufixo + ".csv"));
        salvarCentroides(resultado.getCentroides(), normalizador, normalizar,
                pasta.resolve("centroides_" + sufixo + ".csv"));
        salvarAtribuicoes(jogadores, resultado.getGrupos(), pasta.resolve("atribuicoes_" + sufixo + ".csv"));
        System.out.printf(Locale.ROOT, "K-Means K=3 | min-max=%s | semente=%d | inércia=%.6f | pureza=%.2f%%%n",
                normalizar, resultado.getSemente(), resultado.getInercia(), pureza * 100);
    }

    public static int[][] contarPerfis(List<Jogador> jogadores, int[] grupos, int k) {
        if (jogadores.size() != grupos.length) {
            throw new IllegalArgumentException("Cada jogador deve ter um grupo.");
        }
        int[][] tabela = new int[k][Perfil.values().length];
        for (int i = 0; i < grupos.length; i++) {
            tabela[grupos[i]][jogadores.get(i).getPerfil().ordinal()]++;
        }
        return tabela;
    }

    public static double calcularPureza(int[][] tabela) {
        int total = 0;
        int predominantes = 0;
        for (int[] linha : tabela) {
            int maior = 0;
            for (int quantidade : linha) {
                total += quantidade;
                maior = Math.max(maior, quantidade);
            }
            predominantes += maior;
        }
        if (total == 0) {
            throw new IllegalArgumentException("Não é possível avaliar grupos sem jogadores.");
        }
        return (double) predominantes / total;
    }

    private static void salvarGrupos(int[][] tabela, Path arquivo) throws IOException {
        try (PrintWriter saida = escritor(arquivo)) {
            saida.println("grupo;tamanho;CASUAL;REGULAR;RANQUEADO");
            for (int grupo = 0; grupo < tabela.length; grupo++) {
                int[] linha = tabela[grupo];
                int total = linha[0] + linha[1] + linha[2];
                saida.printf("%d;%d;%d;%d;%d%n", grupo + 1, total, linha[0], linha[1], linha[2]);
            }
            conferirEscrita(saida);
        }
    }

    private static void salvarCentroides(double[][] centroides, NormalizadorMinMax normalizador,
                                          boolean normalizar, Path arquivo) throws IOException {
        try (PrintWriter saida = escritor(arquivo)) {
            saida.println("grupo;horas_semana;partidas_semana;gasto_mensal;taxa_vitoria");
            for (int grupo = 0; grupo < centroides.length; grupo++) {
                double[] c = normalizar ? normalizador.desnormalizar(centroides[grupo]) : centroides[grupo];
                saida.printf(Locale.ROOT, "%d;%.8f;%.8f;%.8f;%.8f%n", grupo + 1, c[0], c[1], c[2], c[3]);
            }
            conferirEscrita(saida);
        }
    }

    private static void salvarAtribuicoes(List<Jogador> jogadores, int[] grupos,
                                          Path arquivo) throws IOException {
        try (PrintWriter saida = escritor(arquivo)) {
            saida.println("id;apelido;perfil_real;grupo");
            for (int i = 0; i < jogadores.size(); i++) {
                Jogador j = jogadores.get(i);
                saida.printf("%d;\"%s\";%s;%d%n", j.getId(), j.getApelido().replace("\"", "\"\""),
                        j.getPerfil(), grupos[i] + 1);
            }
            conferirEscrita(saida);
        }
    }

    private static PrintWriter escritor(Path arquivo) throws IOException {
        return new PrintWriter(Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8));
    }

    private static void conferirEscrita(PrintWriter saida) throws IOException {
        if (saida.checkError()) {
            throw new IOException("Falha ao gravar um arquivo de resultados.");
        }
    }
}
