import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KMeans {
    private final int k;
    private final int limiteIteracoes;
    private final long semente;

    public KMeans(int k, int limiteIteracoes, long semente) {
        if (k < 1 || limiteIteracoes < 1) {
            throw new IllegalArgumentException("K e limite de iterações devem ser positivos.");
        }
        this.k = k;
        this.limiteIteracoes = limiteIteracoes;
        this.semente = semente;
    }

    public Resultado executar(double[][] dados) {
        validar(dados);
        double[][] centroides = sortearCentroides(dados);
        int[] grupos = new int[dados.length];
        Arrays.fill(grupos, -1);
        boolean convergiu = false;
        int iteracoes = 0;
        while (iteracoes < limiteIteracoes && !convergiu) {
            int[] anteriores = grupos.clone();
            atribuirGrupos(dados, centroides, grupos);
            tratarGruposVazios(dados, centroides, grupos);
            centroides = calcularCentroides(dados, grupos);
            iteracoes++;
            convergiu = Arrays.equals(anteriores, grupos);
        }
        double inercia = 0;
        for (int i = 0; i < dados.length; i++) {
            inercia += distanciaQuadrada(dados[i], centroides[grupos[i]]);
        }
        return new Resultado(grupos, centroides, inercia, iteracoes, convergiu, semente);
    }

    private void validar(double[][] dados) {
        if (dados == null || dados.length < k || dados[0] == null || dados[0].length == 0) {
            throw new IllegalArgumentException("São necessários ao menos K pontos com características.");
        }
        int dimensoes = dados[0].length;
        for (double[] ponto : dados) {
            if (ponto == null || ponto.length != dimensoes) {
                throw new IllegalArgumentException("Todos os pontos devem ter a mesma dimensão.");
            }
            for (double valor : ponto) {
                if (!Double.isFinite(valor)) {
                    throw new IllegalArgumentException("Os valores devem ser finitos.");
                }
            }
        }
    }

    private double[][] sortearCentroides(double[][] dados) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < dados.length; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, new Random(semente));
        double[][] centroides = new double[k][];
        for (int grupo = 0; grupo < k; grupo++) {
            centroides[grupo] = dados[indices.get(grupo)].clone();
        }
        return centroides;
    }

    private void atribuirGrupos(double[][] dados, double[][] centroides, int[] grupos) {
        for (int i = 0; i < dados.length; i++) {
            int maisProximo = 0;
            double menorDistancia = distanciaQuadrada(dados[i], centroides[0]);
            for (int grupo = 1; grupo < k; grupo++) {
                double distancia = distanciaQuadrada(dados[i], centroides[grupo]);
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    maisProximo = grupo;
                }
            }
            grupos[i] = maisProximo;
        }
    }

    private void tratarGruposVazios(double[][] dados, double[][] centroides, int[] grupos) {
        int[] tamanhos = new int[k];
        for (int grupo : grupos) {
            tamanhos[grupo]++;
        }
        for (int vazio = 0; vazio < k; vazio++) {
            if (tamanhos[vazio] != 0) {
                continue;
            }
            int escolhido = -1;
            double maiorDistancia = -1;
            for (int i = 0; i < dados.length; i++) {
                if (tamanhos[grupos[i]] > 1) {
                    double distancia = distanciaQuadrada(dados[i], centroides[grupos[i]]);
                    if (distancia > maiorDistancia) {
                        maiorDistancia = distancia;
                        escolhido = i;
                    }
                }
            }
            // Retira o ponto mais distante de um grupo que ainda terá outros pontos.
            tamanhos[grupos[escolhido]]--;
            grupos[escolhido] = vazio;
            tamanhos[vazio]++;
        }
    }

    private double[][] calcularCentroides(double[][] dados, int[] grupos) {
        double[][] centroides = new double[k][dados[0].length];
        int[] tamanhos = new int[k];
        for (int i = 0; i < dados.length; i++) {
            int grupo = grupos[i];
            tamanhos[grupo]++;
            for (int coluna = 0; coluna < dados[i].length; coluna++) {
                centroides[grupo][coluna] += dados[i][coluna];
            }
        }
        for (int grupo = 0; grupo < k; grupo++) {
            for (int coluna = 0; coluna < centroides[grupo].length; coluna++) {
                centroides[grupo][coluna] /= tamanhos[grupo];
            }
        }
        return centroides;
    }

    private double distanciaQuadrada(double[] a, double[] b) {
        double soma = 0;
        for (int i = 0; i < a.length; i++) {
            double diferenca = a[i] - b[i];
            soma += diferenca * diferenca;
        }
        return soma;
    }

    public static final class Resultado {
        private final int[] grupos;
        private final double[][] centroides;
        private final double inercia;
        private final int iteracoes;
        private final boolean convergiu;
        private final long semente;

        private Resultado(int[] grupos, double[][] centroides, double inercia,
                          int iteracoes, boolean convergiu, long semente) {
            this.grupos = grupos.clone();
            this.centroides = copiar(centroides);
            this.inercia = inercia;
            this.iteracoes = iteracoes;
            this.convergiu = convergiu;
            this.semente = semente;
        }

        public int[] getGrupos() { return grupos.clone(); }
        public double[][] getCentroides() { return copiar(centroides); }
        public double getInercia() { return inercia; }
        public int getIteracoes() { return iteracoes; }
        public boolean isConvergiu() { return convergiu; }
        public long getSemente() { return semente; }

        private static double[][] copiar(double[][] matriz) {
            double[][] copia = new double[matriz.length][];
            for (int i = 0; i < matriz.length; i++) {
                copia[i] = matriz[i].clone();
            }
            return copia;
        }
    }
}
