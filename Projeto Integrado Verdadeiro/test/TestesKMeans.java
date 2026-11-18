import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class TestesKMeans {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        testarGeometria();
        testarGruposVazios();
        testarValidacoes();
        testarMetricas();
        try (Connection conexao = BancoDados.abrir()) {
            testarJogadores(new JogadorDAOJdbc(conexao).listarTodos());
        }
        System.out.println("OK: " + verificacoes + " verificações de K-Means passaram.");
    }

    private static void testarGeometria() {
        double[][] dados = {{0}, {2}, {10}, {12}};
        KMeans.Resultado r = new KMeans(2, 100, 42).executar(dados);
        int[] grupos = r.getGrupos();
        conferir(grupos[0] == grupos[1] && grupos[2] == grupos[3] && grupos[0] != grupos[2],
                "Dois pares separados");
        conferir(proximo(r.getInercia(), 4), "Inércia conhecida igual a 4");
        conferir(proximo(r.getCentroides()[grupos[0]][0], 1), "Primeiro centroide na média 1");
        conferir(proximo(r.getCentroides()[grupos[2]][0], 11), "Segundo centroide na média 11");
        conferir(r.isConvergiu() && r.getIteracoes() <= 100, "Convergência dentro do limite");
        KMeans.Resultado repetido = new KMeans(2, 100, 42).executar(dados);
        conferir(Arrays.equals(grupos, repetido.getGrupos())
                && Arrays.deepEquals(r.getCentroides(), repetido.getCentroides()), "Semente reproduz grupos e centroides");
        conferir(Arrays.deepEquals(dados, new double[][] {{0}, {2}, {10}, {12}}), "Entrada não foi alterada");
        int[] copia = r.getGrupos();
        copia[0] = -1;
        conferir(r.getGrupos()[0] >= 0, "Grupos protegidos por cópia");
        double[][] centros = r.getCentroides();
        centros[0][0] = 999;
        conferir(r.getCentroides()[0][0] != 999, "Centroides protegidos por cópia profunda");
        KMeans.Resultado limite = new KMeans(2, 1, 42).executar(dados);
        conferir(limite.getIteracoes() == 1 && !limite.isConvergiu(), "Limite interrompe sem afirmar convergência");
        KMeans.Resultado unico = new KMeans(1, 100, 7).executar(dados);
        conferir(proximo(unico.getCentroides()[0][0], 6) && proximo(unico.getInercia(), 104), "K=1: média global e inércia");
        conferir(new KMeans(4, 100, 7).executar(dados).getInercia() == 0, "K igual ao número de pontos");
    }

    private static void testarGruposVazios() {
        double[][] iguais = {{2, 2}, {2, 2}, {2, 2}, {2, 2}};
        KMeans.Resultado r = new KMeans(3, 100, 7).executar(iguais);
        int[] tamanhos = new int[3];
        for (int grupo : r.getGrupos()) tamanhos[grupo]++;
        for (int tamanho : tamanhos) conferir(tamanho > 0, "Grupos vazios recuperados mesmo com pontos iguais");
        conferir(r.getInercia() == 0 && r.isConvergiu(), "Pontos iguais convergem com inércia zero");
        for (double[] c : r.getCentroides()) {
            conferir(c[0] == 2 && c[1] == 2, "Sem NaN em grupos recuperados");
        }
    }

    private static void testarValidacoes() {
        esperar(() -> new KMeans(0, 100, 42));
        esperar(() -> new KMeans(1, 0, 42));
        esperar(() -> new KMeans(2, 100, 42).executar(new double[][] {{1}}));
        esperar(() -> new KMeans(1, 100, 42).executar(null));
        esperar(() -> new KMeans(1, 100, 42).executar(new double[][] {{}}));
        esperar(() -> new KMeans(1, 100, 42).executar(new double[][] {{1}, {1, 2}}));
        esperar(() -> new KMeans(1, 100, 42).executar(new double[][] {{Double.NaN}}));
    }

    private static void testarMetricas() {
        conferir(proximo(ExperimentosKMeans.calcularPureza(new int[][] {{2, 1, 0}, {0, 1, 3}}), 5.0 / 7),
                "Pureza conhecida: 5/7");
        esperar(() -> ExperimentosKMeans.calcularPureza(new int[][] {{0, 0, 0}}));
    }

    private static void testarJogadores(List<Jogador> jogadores) {
        NormalizadorMinMax normalizador = new NormalizadorMinMax();
        normalizador.encontrarMinMax(jogadores);
        double[][] dados = new double[jogadores.size()][];
        for (int i = 0; i < dados.length; i++) {
            double[] original = jogadores.get(i).getCaracteristicas();
            dados[i] = normalizador.normalizar(original);
            double[] restaurado = normalizador.desnormalizar(dados[i]);
            for (int coluna = 0; coluna < 4; coluna++) {
                conferir(proximo(original[coluna], restaurado[coluna]), "Ida e volta do min-max");
            }
        }
        KMeans.Resultado r = new KMeans(3, 100, 42).executar(dados);
        int[] grupos = r.getGrupos();
        double[][] centroides = r.getCentroides();
        int[] tamanhos = new int[3];
        double[][] somas = new double[3][4];
        double inercia = 0;
        for (int i = 0; i < dados.length; i++) {
            conferir(grupos[i] >= 0 && grupos[i] < 3, "Grupo válido");
            tamanhos[grupos[i]]++;
            for (int coluna = 0; coluna < 4; coluna++) {
                somas[grupos[i]][coluna] += dados[i][coluna];
                double d = dados[i][coluna] - centroides[grupos[i]][coluna];
                inercia += d * d;
            }
        }
        conferir(grupos.length == 150, "Todos os 150 jogadores agrupados");
        conferir(proximo(inercia, r.getInercia()), "Inércia confere com soma independente");
        for (int grupo = 0; grupo < 3; grupo++) {
            conferir(tamanhos[grupo] > 0, "Grupo não vazio no banco real");
            for (int coluna = 0; coluna < 4; coluna++) {
                conferir(proximo(somas[grupo][coluna] / tamanhos[grupo], centroides[grupo][coluna]),
                        "Centroide é a média das características");
            }
        }
        int[][] tabela = ExperimentosKMeans.contarPerfis(jogadores, grupos, 3);
        int total = 0;
        int predominantes = 0;
        for (int[] linha : tabela) {
            total += linha[0] + linha[1] + linha[2];
            predominantes += Math.max(linha[0], Math.max(linha[1], linha[2]));
        }
        conferir(total == 150 && proximo(ExperimentosKMeans.calcularPureza(tabela), predominantes / 150.0),
                "Tabela de perfis e pureza consistentes");
        NormalizadorMinMax constante = new NormalizadorMinMax();
        constante.encontrarMinMax(List.of(jogadores.get(0)));
        conferir(Arrays.equals(constante.desnormalizar(new double[4]), jogadores.get(0).getCaracteristicas()),
                "Desnormalização de coluna constante");
    }

    private static boolean proximo(double a, double b) { return Math.abs(a - b) < 1e-8; }

    private static void conferir(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static void esperar(Runnable acao) {
        verificacoes++;
        try { acao.run(); } catch (IllegalArgumentException e) { return; }
        throw new AssertionError("Esperada IllegalArgumentException");
    }
}
