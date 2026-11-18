import java.util.ArrayList;
import java.util.List;

public class AvaliacaoKNN {
    private final List<Previsao> previsoes;
    private final int[][] matriz;
    private final int acertos;

    public AvaliacaoKNN(KNN knn, List<Jogador> teste) {
        if (teste == null || teste.isEmpty()) {
            throw new IllegalArgumentException("O teste não pode ser vazio.");
        }
        List<Previsao> resultados = new ArrayList<>();
        matriz = new int[Perfil.values().length][Perfil.values().length];
        int totalAcertos = 0;
        for (Jogador jogador : teste) {
            Perfil previsto = knn.classificar(jogador.getCaracteristicas());
            resultados.add(new Previsao(jogador, previsto));
            matriz[jogador.getPerfil().ordinal()][previsto.ordinal()]++;
            if (jogador.getPerfil() == previsto) {
                totalAcertos++;
            }
        }
        previsoes = List.copyOf(resultados);
        acertos = totalAcertos;
    }

    public int getAcertos() {
        return acertos;
    }

    public double getAcuracia() {
        return (double) acertos / previsoes.size();
    }

    public int[][] getMatrizConfusao() {
        int[][] copia = new int[matriz.length][];
        for (int i = 0; i < matriz.length; i++) {
            copia[i] = matriz[i].clone();
        }
        return copia;
    }

    public List<Previsao> getPrevisoes() {
        return previsoes;
    }

    public List<Previsao> getErros() {
        List<Previsao> erros = new ArrayList<>();
        for (Previsao previsao : previsoes) {
            if (previsao.getJogador().getPerfil() != previsao.getPerfilPrevisto()) {
                erros.add(previsao);
            }
        }
        return List.copyOf(erros);
    }

    public static class Previsao {
        private final Jogador jogador;
        private final Perfil perfilPrevisto;

        private Previsao(Jogador jogador, Perfil perfilPrevisto) {
            this.jogador = jogador;
            this.perfilPrevisto = perfilPrevisto;
        }

        public Jogador getJogador() {
            return jogador;
        }

        public Perfil getPerfilPrevisto() {
            return perfilPrevisto;
        }
    }
}
