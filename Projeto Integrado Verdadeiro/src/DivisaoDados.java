import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class DivisaoDados {
    private final List<Jogador> treino;
    private final List<Jogador> teste;

    public DivisaoDados(List<Jogador> jogadores, long semente) {
        if (jogadores == null || jogadores.size() < 2) {
            throw new IllegalArgumentException("São necessários ao menos dois jogadores.");
        }
        List<Jogador> embaralhados = new ArrayList<>(jogadores);
        embaralhados.sort(Comparator.comparingInt(Jogador::getId));
        Collections.shuffle(embaralhados, new Random(semente));
        int tamanhoTreino = jogadores.size() * 80 / 100;
        treino = List.copyOf(embaralhados.subList(0, tamanhoTreino));
        teste = List.copyOf(embaralhados.subList(tamanhoTreino, embaralhados.size()));
    }

    public List<Jogador> getTreino() {
        return treino;
    }

    public List<Jogador> getTeste() {
        return teste;
    }
}
