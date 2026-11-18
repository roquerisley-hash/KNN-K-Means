import java.util.List;

public class NormalizadorMinMax {

    private double[] mins;
    private double[] maxs;

    public void encontrarMinMax(List<Jogador> jogadores) {

        if (jogadores == null || jogadores.isEmpty()) {
            throw new IllegalArgumentException("Lista de jogadores vazia.");
        }

        int numCaracteristicas = jogadores.get(0).getCaracteristicas().length;

        mins = new double[numCaracteristicas];
        maxs = new double[numCaracteristicas];

        double[] primeiro = jogadores.get(0).getCaracteristicas();

        for (int i = 0; i < numCaracteristicas; i++) {
            mins[i] = primeiro[i];
            maxs[i] = primeiro[i];
        }

        for (Jogador jogador : jogadores) {

            double[] caracteristicas = jogador.getCaracteristicas();

            for (int i = 0; i < numCaracteristicas; i++) {
                mins[i] = Math.min(mins[i], caracteristicas[i]);
                maxs[i] = Math.max(maxs[i], caracteristicas[i]);
            }
        }
    }

    public double[] normalizar(double[] caracteristicas) {

        if (mins == null || maxs == null) {
            throw new IllegalStateException("Min e Max não foram encontrados " +
                    "(chame encontrarMinMax() primeiro).");
        }

        if (caracteristicas == null || caracteristicas.length != mins.length) {
            throw new IllegalArgumentException("Características inválidas.");
        }

        double[] normalizadas = new double[caracteristicas.length];

        for (int i = 0; i < caracteristicas.length; i++) {

            if (maxs[i] - mins[i] == 0) {
                normalizadas[i] = 0.0;
            } else {
                normalizadas[i] = (caracteristicas[i] - mins[i])
                        / (maxs[i] - mins[i]);
            }
        }

        return normalizadas;
    }
}
