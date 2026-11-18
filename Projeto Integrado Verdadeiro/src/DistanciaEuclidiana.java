public class DistanciaEuclidiana implements MedidaDeDistancia {

    @Override
    public double calcular(double[] jogador1, double[] jogador2) {
        if (jogador1.length != jogador2.length) {
            throw new IllegalArgumentException("Os pontos devem ter a mesma dimensão.");
        }

        double soma = 0.0;
        for (int i = 0; i < jogador1.length; i++) {
            soma += Math.pow(jogador1[i] - jogador2[i], 2);
        }
        return Math.sqrt(soma);
    }
}
