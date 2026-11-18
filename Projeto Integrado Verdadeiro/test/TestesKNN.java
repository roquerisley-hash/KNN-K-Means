import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestesKNN {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        testarDistancias();
        testarNormalizador();
        testarVotacao();
        testarValidacoes();
        try (Connection conexao = DriverManager.getConnection("jdbc:h2:mem:testes", "sa", "")) {
            try (PreparedStatement stmt = conexao.prepareStatement("RUNSCRIPT FROM 'jogadores.sql'")) {
                stmt.execute();
            }
            testarBancoEExperimentos(new JogadorDAOJdbc(conexao));
        }
        System.out.println("OK: " + verificacoes + " verificações passaram.");
    }

    private static void testarDistancias() {
        double[] a = {0, 0, 0, 0};
        double[] b = {3, 4, 0, 0};
        conferir(proximo(new DistanciaEuclidiana().calcular(a, b), 5), "Euclidiana 3-4-5");
        conferir(proximo(new DistanciaManhattan().calcular(a, b), 7), "Manhattan = 7");
        for (MedidaDeDistancia distancia : new MedidaDeDistancia[] {
                new DistanciaEuclidiana(), new DistanciaManhattan()}) {
            conferir(distancia.calcular(b, b) == 0, "Distância do ponto a si mesmo");
            conferir(distancia.calcular(a, b) == distancia.calcular(b, a), "Simetria");
            esperar(IllegalArgumentException.class, () -> distancia.calcular(a, new double[3]));
        }
    }

    private static void testarNormalizador() {
        List<Jogador> treino = Arrays.asList(jogador(1, 0, Perfil.CASUAL),
                jogador(2, 10, Perfil.REGULAR));
        NormalizadorMinMax normalizador = new NormalizadorMinMax();
        esperar(IllegalStateException.class, () -> normalizador.normalizar(new double[4]));
        esperar(IllegalArgumentException.class, () -> normalizador.encontrarMinMax(List.of()));
        normalizador.encontrarMinMax(treino);
        conferir(proximo(normalizador.normalizar(new double[] {5, 0, 0, 0})[0], 0.5), "Min-max intermediário");
        conferir(normalizador.normalizar(new double[] {0, 0, 0, 0})[0] == 0, "Mínimo = 0");
        conferir(normalizador.normalizar(new double[] {10, 0, 0, 0})[0] == 1, "Máximo = 1");
        double[] fora = normalizador.normalizar(new double[] {20, 9, 9, 9});
        conferir(fora[0] == 2, "Teste usa parâmetros do treino, sem limitar a 1");
        conferir(fora[1] == 0 && fora[2] == 0 && fora[3] == 0, "Colunas constantes não dividem por zero");
        conferir(normalizador.normalizar(new double[] {5, 0, 0, 0})[0] == 0.5, "Consulta não reajusta min-max");
        esperar(IllegalArgumentException.class, () -> normalizador.normalizar(new double[3]));
        KNN knn = new KNN(treino, 1, new DistanciaEuclidiana(), true);
        conferir(proximo(knn.encontrarVizinhos(new double[] {20, 0, 0, 0}).get(0).getDistancia(), 1),
                "KNN ajusta normalizador somente no treino");
        double[] copia = treino.get(0).getCaracteristicas();
        copia[0] = 999;
        conferir(treino.get(0).getHorasSemana() == 0, "Jogador protege suas características");
    }

    private static void testarVotacao() {
        List<Jogador> treino = new ArrayList<>(Arrays.asList(jogador(1, 1, Perfil.CASUAL),
                jogador(2, 2, Perfil.REGULAR), jogador(3, 3, Perfil.REGULAR)));
        double[] consulta = {0, 0, 0, 0};
        KNN maioria = new KNN(treino, 3, new DistanciaEuclidiana(), false);
        conferir(maioria.classificar(consulta) == Perfil.REGULAR, "Maioria vence o vizinho isolado");
        KNN empate = new KNN(treino, 2, new DistanciaEuclidiana(), false);
        conferir(empate.classificar(consulta) == Perfil.CASUAL, "Empate vence perfil mais próximo");
        List<Jogador> iguais = Arrays.asList(jogador(30, 1, Perfil.CASUAL),
                jogador(10, -1, Perfil.RANQUEADO), jogador(20, 1, Perfil.REGULAR));
        KNN triplo = new KNN(iguais, 3, new DistanciaEuclidiana(), false);
        conferir(triplo.classificar(consulta) == Perfil.RANQUEADO, "Empate triplo usa menor ID na distância igual");
        conferir(new KNN(iguais, 1, new DistanciaEuclidiana(), false).encontrarVizinhos(consulta)
                .get(0).getJogador().getId() == 10, "Corte em K com distâncias iguais usa ID");
        Collections.reverse(treino);
        conferir(new KNN(treino, 2, new DistanciaEuclidiana(), false).classificar(consulta)
                == empate.classificar(consulta), "Votação não depende da ordem de entrada");
        treino.clear();
        conferir(maioria.classificar(consulta) == Perfil.REGULAR, "KNN preserva cópia do treino");
        esperar(UnsupportedOperationException.class, () -> maioria.encontrarVizinhos(consulta).clear());

        List<Jogador> pontos = Arrays.asList(new Jogador(1, "A", 3, 0, 0, 0, Perfil.CASUAL),
                new Jogador(2, "B", 2, 2, 0, 0, Perfil.REGULAR));
        conferir(new KNN(pontos, 1, new DistanciaEuclidiana(), false).classificar(consulta)
                == Perfil.REGULAR, "KNN usa distância euclidiana injetada");
        conferir(new KNN(pontos, 1, new DistanciaManhattan(), false).classificar(consulta)
                == Perfil.CASUAL, "Troca por Manhattan altera vizinho sem modificar KNN");
    }

    private static void testarValidacoes() {
        List<Jogador> treino = List.of(jogador(1, 1, Perfil.CASUAL));
        esperar(IllegalArgumentException.class, () -> new KNN(treino, 0, new DistanciaEuclidiana(), false));
        esperar(IllegalArgumentException.class, () -> new KNN(treino, 2, new DistanciaEuclidiana(), false));
        esperar(IllegalArgumentException.class, () -> new KNN(List.of(), 1, new DistanciaEuclidiana(), false));
        esperar(IllegalArgumentException.class, () -> new KNN(treino, 1, null, false));
        KNN knn = new KNN(treino, 1, new DistanciaEuclidiana(), false);
        esperar(IllegalArgumentException.class, () -> knn.classificar(new double[3]));
        esperar(IllegalArgumentException.class, () -> knn.classificar(new double[] {Double.NaN, 0, 0, 0}));
        esperar(IllegalArgumentException.class, () -> new AvaliacaoKNN(knn, List.of()));
        esperar(IllegalArgumentException.class, () -> new DivisaoDados(List.of(), 42));
    }

    private static void testarBancoEExperimentos(JogadorDAO dao) {
        List<Jogador> jogadores = dao.listarTodos();
        conferir(jogadores.size() == 150, "H2 e DAO carregam 150 registros");
        conferir(dao.buscarPorId(130).get().getApelido().equals("CrimsonNinja12"), "DAO busca por ID");
        conferir(dao.buscarPorId(999).isEmpty(), "DAO retorna ausência para ID inexistente");
        for (Perfil perfil : Perfil.values()) {
            conferir(dao.listarPorPerfil(perfil).size() == 50, "50 jogadores por perfil: " + perfil);
        }
        DivisaoDados divisao = new DivisaoDados(jogadores, AppKNN.SEMENTE);
        conferir(divisao.getTreino().size() == 120 && divisao.getTeste().size() == 30, "Divisão 120/30");
        Set<Jogador> todos = new HashSet<>(divisao.getTreino());
        conferir(Collections.disjoint(todos, divisao.getTeste()), "Sem sobreposição de treino e teste");
        todos.addAll(divisao.getTeste());
        conferir(todos.size() == 150, "Divisão não perde registros");
        conferir(jogadores.get(0).getId() == 1, "Divisão não muda a lista original");
        List<Jogador> invertida = new ArrayList<>(jogadores);
        Collections.reverse(invertida);
        DivisaoDados repetida = new DivisaoDados(invertida, AppKNN.SEMENTE);
        conferir(repetida.getTreino().equals(divisao.getTreino())
                && repetida.getTeste().equals(divisao.getTeste()), "Semente reproduz a divisão pela ordem de IDs");
        esperar(UnsupportedOperationException.class, () -> divisao.getTreino().clear());

        for (MedidaDeDistancia medida : new MedidaDeDistancia[] {
                new DistanciaEuclidiana(), new DistanciaManhattan()}) {
            for (boolean normalizar : new boolean[] {true, false}) {
                for (int k : new int[] {1, 3, 5, 7, 9, 11}) {
                    KNN knn = new KNN(divisao.getTreino(), k, medida, normalizar);
                    AvaliacaoKNN resultado = new AvaliacaoKNN(knn, divisao.getTeste());
                    int[][] matriz = new int[3][3];
                    int acertos = 0;
                    for (Jogador jogador : divisao.getTeste()) {
                        Perfil previsto = knn.classificar(jogador.getCaracteristicas());
                        matriz[jogador.getPerfil().ordinal()][previsto.ordinal()]++;
                        if (jogador.getPerfil() == previsto) acertos++;
                    }
                    conferir(Arrays.deepEquals(matriz, resultado.getMatrizConfusao()), "Matriz confere com previsões");
                    conferir(resultado.getAcertos() == acertos && proximo(resultado.getAcuracia(), acertos / 30.0),
                            "Acurácia confere com contagem direta");
                    conferir(resultado.getPrevisoes().size() == 30 && resultado.getErros().size() == 30 - acertos,
                            "Lista de erros e total consistentes");
                    conferir(knn.encontrarVizinhos(new double[] {15, 35, 80, 52}).size() == k,
                            "K vizinhos para jogador novo");
                    int[][] copia = resultado.getMatrizConfusao();
                    copia[0][0] = -1;
                    conferir(resultado.getMatrizConfusao()[0][0] >= 0, "Matriz protegida de alteração externa");
                }
            }
        }
        KNN principal = new KNN(divisao.getTreino(), 5, new DistanciaEuclidiana(), true);
        AvaliacaoKNN resultado = new AvaliacaoKNN(principal, divisao.getTeste());
        conferir(resultado.getAcertos() == 29, "Regressão: modelo principal acerta 29/30");
        conferir(resultado.getErros().get(0).getJogador().getId() == 130, "Erro principal disponível para Samuel");
        conferir(principal.classificar(new double[] {15, 35, 80, 52}) == Perfil.REGULAR,
                "Jogador novo recebe perfil REGULAR");
    }

    private static Jogador jogador(int id, double horas, Perfil perfil) {
        return new Jogador(id, "Jogador" + id, horas, 0, 0, 0, perfil);
    }

    private static boolean proximo(double a, double b) {
        return Math.abs(a - b) < 1e-9;
    }

    private static void conferir(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static void esperar(Class<? extends Throwable> tipo, Runnable acao) {
        verificacoes++;
        try {
            acao.run();
        } catch (Throwable erro) {
            if (tipo.isInstance(erro)) return;
            throw new AssertionError("Exceção inesperada: " + erro, erro);
        }
        throw new AssertionError("Esperada exceção " + tipo.getSimpleName());
    }
}
