import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KNN {
    private final List<Jogador> treino;
    private final List<double[]> caracteristicasTreino;
    private final int k;
    private final MedidaDeDistancia distancia;
    private final NormalizadorMinMax normalizador;

    public KNN(List<Jogador> treino, int k, MedidaDeDistancia distancia,
               boolean normalizar) {
        if (treino == null || treino.isEmpty()) {
            throw new IllegalArgumentException("O treino não pode ser vazio.");
        }
        if (k < 1 || k > treino.size()) {
            throw new IllegalArgumentException("K deve estar entre 1 e o tamanho do treino.");
        }
        if (distancia == null) {
            throw new IllegalArgumentException("Informe a medida de distância.");
        }
        this.treino = List.copyOf(treino);
        this.k = k;
        this.distancia = distancia;
        for (Jogador jogador : this.treino) {
            validarCaracteristicas(jogador.getCaracteristicas());
        }
        normalizador = normalizar ? new NormalizadorMinMax() : null;
        if (normalizador != null) {

            normalizador.encontrarMinMax(this.treino);
        }
        caracteristicasTreino = new ArrayList<>();
        for (Jogador jogador : this.treino) {
            caracteristicasTreino.add(preparar(jogador.getCaracteristicas()));
        }
    }

    public Perfil classificar(double[] caracteristicas) {
        List<Vizinho> vizinhos = encontrarVizinhos(caracteristicas);
        int[] votos = new int[Perfil.values().length];
        int maiorVotacao = 0;
        for (Vizinho vizinho : vizinhos) {
            int indice = vizinho.getJogador().getPerfil().ordinal();
            votos[indice]++;
            maiorVotacao = Math.max(maiorVotacao, votos[indice]);
        }

        for (Vizinho vizinho : vizinhos) {
            Perfil perfil = vizinho.getJogador().getPerfil();
            if (votos[perfil.ordinal()] == maiorVotacao) {
                return perfil;
            }
        }
        throw new IllegalStateException("Não foi possível classificar.");
    }

    public List<Vizinho> encontrarVizinhos(double[] caracteristicas) {
        validarCaracteristicas(caracteristicas);
        double[] consulta = preparar(caracteristicas);
        List<Vizinho> vizinhos = new ArrayList<>();
        for (int i = 0; i < treino.size(); i++) {
            double valor = distancia.calcular(consulta, caracteristicasTreino.get(i));
            if (!Double.isFinite(valor) || valor < 0) {
                throw new IllegalArgumentException("A medida retornou uma distância inválida.");
            }
            vizinhos.add(new Vizinho(treino.get(i), valor));
        }

        Collections.sort(vizinhos);
        return List.copyOf(vizinhos.subList(0, k));
    }

    private double[] preparar(double[] caracteristicas) {
        return normalizador == null ? caracteristicas.clone()
                : normalizador.normalizar(caracteristicas);
    }

    private void validarCaracteristicas(double[] caracteristicas) {
        if (caracteristicas == null || caracteristicas.length != 4) {
            throw new IllegalArgumentException("Informe as quatro características do jogador.");
        }
        for (double valor : caracteristicas) {
            if (!Double.isFinite(valor)) {
                throw new IllegalArgumentException("As características devem ser números finitos.");
            }
        }
    }

    public static final class Vizinho implements Comparable<Vizinho> {
        private final Jogador jogador;
        private final double distancia;

        private Vizinho(Jogador jogador, double distancia) {
            this.jogador = jogador;
            this.distancia = distancia;
        }

        public Jogador getJogador() {
            return jogador;
        }

        public double getDistancia() {
            return distancia;
        }

        @Override
        public int compareTo(Vizinho outro) {
            if (distancia < outro.distancia) {
                return -1;
            }
            if (distancia > outro.distancia) {
                return 1;
            }
            return Integer.compare(jogador.getId(), outro.jogador.getId());
        }
    }
}
